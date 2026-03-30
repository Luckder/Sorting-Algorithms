import algorithms.Sort;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.*;

public class SortVisualiser extends Application {

    // Each frame of animation is one recorded state of the list
    private record Frame(int[] values, int swapA, int swapB) {}

    private static final int WIDTH      = 1200;
    private static final int HEIGHT     = 600;
    private static final int BAR_AREA_H = 600;

    private final List<Frame> frames    = new ArrayList<>();
    private int frameIndex              = 0;
    private AnimationTimer timer;
    private long lastFrameTime          = 0;
    private long frameDelayNanos        = 2_000_000L; // ~60fps default

    @Override
    public void start(Stage stage) {
        // Controls
        ComboBox<Sort<Integer>> algorithmPicker = new ComboBox<>();
        Main.load().stream()
                .filter(a -> !(a.toString().equals("CosmicSort")))
                .filter(a -> !(a.toString().equals("BogoSort")))
                .forEach(a -> {
                    @SuppressWarnings("unchecked")
                    Sort<Integer> s = (Sort<Integer>) a;
                    algorithmPicker.getItems().add(s);
                });
        algorithmPicker.getSelectionModel().selectFirst();
        algorithmPicker.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Sort<Integer> s)   { return s == null ? "" : s.toString(); }
            public Sort<Integer> fromString(String s) { return null; }
        });

        Slider speedSlider = new Slider(1, 500, 60);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        Label speedLabel = new Label("Speed");

        Slider sizeSlider = new Slider(10, 500, 100);
        sizeSlider.setShowTickLabels(true);
        Label sizeLabel = new Label("Elements");

        Button runButton = new Button("▶  Sort");

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawEmpty(gc);

        // Wire speed slider to live frame delay changes
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            // Map slider 1–500 to delay 500ms–1ms
            long ms = Math.max(1, (long)(501 - newVal.doubleValue()));
            frameDelayNanos = ms * 100_000L; // was 1_000_000L — 10x faster overall
        });

        runButton.setOnAction(e -> {
            Sort<Integer> sorter = algorithmPicker.getValue();
            if (sorter == null) return;

//            int size = (int) sizeSlider.getValue();
//            List<SimpleEntry<Integer, Integer>> data = Main.makeIntegerList(size);
//
//            // Record all frames by attaching a listener BEFORE sorting
//            frames.clear();
//            frameIndex = 0;
//
//            // Capture initial state
//            frames.add(snapshot(data, -1, -1));
//
//            // sorter.setListener((list, i, j) -> frames.add(snapshot(list, i, j)));
//
//            // Sort on a background thread so we don't block JavaFX
//            new Thread(() -> {
//                sorter.sort(data);
//                // Add a final clean frame with no highlights
//                frames.add(snapshot(data, -1, -1));
//                // Start playback on the JavaFX thread
//                javafx.application.Platform.runLater(() -> playFrames(gc));
//            }).start();

            int size = (int) sizeSlider.getValue();
            List<SimpleEntry<Integer, Integer>> rawData = Main.makeIntegerList(size);

            frames.clear();
            frameIndex = 0;
            frames.add(snapshot(rawData, -1, -1));

// Wrap the list — every set() fires a snapshot regardless of which algorithm
            TrackingList<SimpleEntry<Integer, Integer>> tracked = new TrackingList<>(rawData,
                    (index, value) -> frames.add(snapshot(rawData, index, -1)));

            new Thread(() -> {
                sorter.sort(tracked);
                frames.add(snapshot(rawData, -1, -1));
                javafx.application.Platform.runLater(() -> playFrames(gc));
            }).start();
        });

        HBox controls = new HBox(12,
                algorithmPicker,
                sizeLabel, sizeSlider,
                speedLabel, speedSlider,
                runButton);
        controls.setPadding(new Insets(8));
        controls.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox root = new VBox(controls, canvas);
        stage.setScene(new Scene(root));
        stage.setTitle("Sort Visualiser");
        stage.setResizable(false);
        stage.show();
    }

    private void playFrames(GraphicsContext gc) {
        if (timer != null) timer.stop();
        frameIndex = 0;

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastFrameTime < frameDelayNanos) return;
                lastFrameTime = now;

                if (frameIndex >= frames.size()) {
                    stop();
                    return;
                }
                drawFrame(gc, frames.get(frameIndex++));
            }
        };
        timer.start();
    }

    private void drawFrame(GraphicsContext gc, Frame frame) {
        gc.setFill(Color.rgb(20, 20, 30));
        gc.fillRect(0, 0, WIDTH, BAR_AREA_H);

        int n       = frame.values().length;
        double barW = (double) WIDTH / n;
        int maxVal  = Arrays.stream(frame.values()).max().orElse(1);

        for (int i = 0; i < n; i++) {
            double barH = (double) frame.values()[i] / maxVal * BAR_AREA_H;
            double x    = i * barW;
            double y    = BAR_AREA_H - barH;

            if      (i == frame.swapA() || i == frame.swapB()) gc.setFill(Color.rgb(255, 80,  80));
            else                                                gc.setFill(Color.rgb(80,  180, 255));

            gc.fillRect(x + 1, y, Math.max(barW - 1, 1), barH);
        }
    }

    private void drawEmpty(GraphicsContext gc) {
        gc.setFill(Color.rgb(20, 20, 30));
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        gc.setFill(Color.GRAY);
        gc.fillText("Pick an algorithm and press Sort", WIDTH / 2.0 - 120, HEIGHT / 2.0);
    }

    @SuppressWarnings("unchecked")
    private Frame snapshot(List<?> list, int a, int b) {
        List<SimpleEntry<Integer, Integer>> typed =
                (List<SimpleEntry<Integer, Integer>>) list;
        int[] vals = typed.stream()
                .mapToInt(e -> e.getKey())
                .toArray();
        return new Frame(vals, a, b);
    }

    public static void main(String[] args) {
        launch(args);
    }
}