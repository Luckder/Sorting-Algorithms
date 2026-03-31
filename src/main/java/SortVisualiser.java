import algorithms.Sort;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.locks.LockSupport;

public class SortVisualiser {

    private record Frame(int[] values, int[] originalIndices, int maxValue, int targetIndex, String type, int greenCount) {}
    private record SortedRef(int[] keys, int[] indices) {}

    private static final int WIDTH      = 1200;
    private static final int HEIGHT     = 600;
    private static final int BAR_AREA_H = 600;

    private volatile Frame     currentFrame   = null;
    private volatile SortedRef sortedSnapshot = null;

    private volatile int  sortGeneration  = 0;
    private javax.swing.Timer renderTimer;
    private volatile long frameDelayNanos = 44_100_000L;
    private boolean isBogoSort = false;

    // Timer fields. sortStartNanos == 0 means "no sort has run yet, don't draw timer".
    // sortEndNanos == -1 means "sort is still in progress, show live elapsed time".
    private volatile long sortStartNanos = 0;
    private volatile long sortEndNanos   = -1;

    public void createAndShowGUI() {

        JComboBox<Sort<Integer>> algorithmPicker = new JComboBox<>();
        Main.load().forEach(a -> {
            @SuppressWarnings("unchecked")
            Sort<Integer> s = (Sort<Integer>) a;
            algorithmPicker.addItem(s);
        });

        JSlider speedSlider = new JSlider(0, 500, 60);
        speedSlider.setMajorTickSpacing(100);
        speedSlider.setMinorTickSpacing(25);
        speedSlider.setPaintLabels(true);
        speedSlider.setPaintTicks(true);
        JLabel speedLabel = new JLabel("Speed");

        JSlider sizeSlider = new JSlider(0, 1000, 100);
        sizeSlider.setMajorTickSpacing(200);
        sizeSlider.setMinorTickSpacing(50);
        sizeSlider.setPaintLabels(true);
        sizeSlider.setPaintTicks(true);
        JLabel sizeLabel = new JLabel("Elements");

        JButton runButton = new JButton("▶  Sort");

        JPanel canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                if (currentFrame == null) drawEmpty(g2d);
                else                      drawFrame(g2d, currentFrame);
            }
        };
        canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        speedSlider.addChangeListener(e -> {
            if (speedSlider.getValue() < 1) speedSlider.setValue(1);
            long ms = Math.max(1, (long)(501 - speedSlider.getValue()));
            frameDelayNanos = ms * 100_000L;
        });

        sizeSlider.addChangeListener(e -> {
            if (sizeSlider.getValue() < 2) sizeSlider.setValue(2);
        });

        runButton.addActionListener(e -> {
            @SuppressWarnings("unchecked")
            Sort<Integer> sorter = (Sort<Integer>) algorithmPicker.getSelectedItem();
            if (sorter == null) return;

            final int gen = ++sortGeneration;

            int size = sizeSlider.getValue();
            List<SimpleEntry<Integer, Integer>> rawData = Main.makeIntegerList(size);

            String algName = sorter.toString();
            isBogoSort = algName.equals("BogoSort");

            if (algName.equals("CosmicSort") || algName.equals("MyFirstSort")) {
                sortedSnapshot = null;
                sortStartNanos = 0;   // No timer for these
                currentFrame   = snapshot(rawData, -1, "NONE", 0);
                canvas.repaint();
                return;
            }

            startRenderTimer(canvas);

            Thread sortThread = new Thread(() -> {
                try {
                    // Build stable sorted reference before touching the list.
                    List<SimpleEntry<Integer, Integer>> refList = new ArrayList<>(rawData);
                    refList.sort(Comparator.comparingInt(SimpleEntry::getKey));
                    int[] refKeys    = new int[refList.size()];
                    int[] refIndices = new int[refList.size()];
                    for (int i = 0; i < refList.size(); i++) {
                        refKeys[i]    = refList.get(i).getKey();
                        refIndices[i] = refList.get(i).getValue();
                    }
                    sortedSnapshot = new SortedRef(refKeys, refIndices);

                    // Timer starts now — after setup, just before the sort begins.
                    sortEndNanos   = -1;
                    sortStartNanos = System.nanoTime();

                    if (isBogoSort) {
                        while (sortGeneration == gen) {
                            Collections.shuffle(rawData);
                            currentFrame = snapshot(rawData, -1, "NONE", 0);
                            LockSupport.parkNanos(frameDelayNanos);
                            if (isSorted(rawData)) {
                                // Stop the timer before the sweep — the sort itself is done.
                                sortEndNanos = System.nanoTime();
                                for (int i = 0; i <= rawData.size(); i++) {
                                    if (sortGeneration != gen) return;
                                    currentFrame = snapshot(rawData, -1, "DONE", i);
                                    LockSupport.parkNanos(Math.min(frameDelayNanos, 2_000_000L));
                                }
                                SwingUtilities.invokeLater(renderTimer::stop);
                                return;
                            }
                        }
                        return;
                    }

                    TrackingList<SimpleEntry<Integer, Integer>> tracked = new TrackingList<>(rawData,
                            (index, value) -> {
                                if (sortGeneration != gen) throw new RuntimeException("Cancelled");
                                currentFrame = snapshot(rawData, index, "WRITE", 0);
                                LockSupport.parkNanos(frameDelayNanos);
                            },
                            (index) -> {
                                if (sortGeneration != gen) throw new RuntimeException("Cancelled");
                                currentFrame = snapshot(rawData, index, "READ", 0);
                                LockSupport.parkNanos(frameDelayNanos);
                            });

                    sorter.sort(tracked);

                    // Stop the timer the moment sorting finishes, before the cosmetic sweep.
                    sortEndNanos = System.nanoTime();

                    for (int i = 0; i <= rawData.size(); i++) {
                        if (sortGeneration != gen) return;
                        currentFrame = snapshot(rawData, -1, "DONE", i);
                        LockSupport.parkNanos(Math.min(frameDelayNanos, 2_000_000L));
                    }

                    SwingUtilities.invokeLater(renderTimer::stop);

                } catch (RuntimeException ex) {
                    // Cancelled by a new Run click.
                }
            });
            sortThread.setDaemon(true);
            sortThread.start();
        });

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        controls.add(algorithmPicker);
        controls.add(sizeLabel);
        controls.add(sizeSlider);
        controls.add(speedLabel);
        controls.add(speedSlider);
        controls.add(runButton);

        JFrame frame = new JFrame("Sort Visualiser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(controls, BorderLayout.NORTH);
        frame.add(canvas, BorderLayout.CENTER);

        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void startRenderTimer(JPanel canvas) {
        if (renderTimer != null && renderTimer.isRunning()) return;
        renderTimer = new javax.swing.Timer(16, e -> canvas.repaint());
        renderTimer.start();
    }

    private void drawFrame(Graphics2D g, Frame frame) {
        g.setColor(new Color(20, 20, 30));
        g.fillRect(0, 0, WIDTH, BAR_AREA_H);

        int n       = frame.values().length;
        double barW = (double) WIDTH / n;
        int maxVal  = frame.maxValue();

        SortedRef sorted = sortedSnapshot; // capture volatile reference once

        for (int i = 0; i < n; i++) {
            double barH = (double) frame.values()[i] / maxVal * BAR_AREA_H;
            int x1 = (int)(i       * barW);
            int x2 = (int)((i + 1) * barW);
            int w  = Math.max(x2 - x1, 1);
            int y  = (int)(BAR_AREA_H - barH);
            int h  = (int) barH;

            Color fillColor;

            if (i < frame.greenCount()) {
                // Inside the sweep zone: determine green vs purple per element.
                // Green  = key matches AND original index matches (stable position).
                // Purple = key matches BUT original index differs (sorted but unstable).
                // A key mismatch here would mean the sort produced a wrong result —
                // keep it blue as a silent error indicator rather than asserting.
                boolean keyMatch = sorted != null && frame.values()[i] == sorted.keys()[i];
                boolean idxMatch = sorted != null && frame.originalIndices()[i] == sorted.indices()[i];

                if (keyMatch && idxMatch) {
                    fillColor = new Color(40, 200, 80);         // Green  — correct and stable
                } else if (keyMatch) {
                    fillColor = new Color(160, 60, 220);        // Purple — correct key, wrong stability
                } else {
                    fillColor = new Color(80, 180, 255);        // Blue   — should not happen post-sort
                }
            } else {
                // Outside the sweep zone: live position check during sorting.
                boolean liveGreen = sorted != null
                        && frame.values()[i]          == sorted.keys()[i]
                        && frame.originalIndices()[i] == sorted.indices()[i];

                if (liveGreen) {
                    fillColor = new Color(40, 200, 80);
                } else if (i == frame.targetIndex() && "WRITE".equals(frame.type())) {
                    fillColor = new Color(255, 80, 80);
                } else if (i == frame.targetIndex() && "READ".equals(frame.type())) {
                    fillColor = new Color(255, 255, 80);
                } else {
                    fillColor = new Color(80, 180, 255);
                }
            }

            g.setColor(fillColor);
            g.fillRect(x1, y, w, h);

            if (w >= 4) {
                g.setColor(new Color(0, 0, 0, 150));
                g.drawRect(x1, y, w - 1, h - 1);
            }
        }

        // Timer overlay — drawn last so it sits on top of the bars.
        drawTimer(g);
    }

    private void drawTimer(Graphics2D g) {
        long start = sortStartNanos;
        if (start == 0) return; // No sort has run yet.

        long end     = sortEndNanos;
        long elapsed = (end < 0) ? System.nanoTime() - start : end - start;
        String label = "Time: " + Main.getTime(elapsed);

        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(label);
        int th = fm.getHeight();

        // Semi-transparent dark pill behind the text for readability over any bar colour.
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRoundRect(8, 8, tw + 14, th + 8, 8, 8);

        g.setColor(Color.WHITE);
        g.drawString(label, 15, 8 + fm.getAscent() + 4);
    }

    private void drawEmpty(Graphics2D g) {
        g.setColor(new Color(20, 20, 30));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.GRAY);
        g.drawString("Pick an algorithm and press Sort", WIDTH / 2 - 120, HEIGHT / 2);
    }

    private Frame snapshot(List<SimpleEntry<Integer, Integer>> list, int target, String type, int greenCount) {
        int[] vals    = new int[list.size()];
        int[] origIdx = new int[list.size()];
        int max = 0;
        for (int i = 0; i < vals.length; i++) {
            vals[i]    = list.get(i).getKey();
            origIdx[i] = list.get(i).getValue();
            if (vals[i] > max) max = vals[i];
        }
        return new Frame(vals, origIdx, max, target, type, greenCount);
    }

    private boolean isSorted(List<SimpleEntry<Integer, Integer>> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).getKey() > list.get(i + 1).getKey()) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SortVisualiser().createAndShowGUI());
    }
}