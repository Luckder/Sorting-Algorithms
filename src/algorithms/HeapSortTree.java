package algorithms;
import java.util.*;
import  java.util.AbstractMap.SimpleEntry;

// Author: David Chan (Luckder)

public class HeapSortTree<T extends  Comparable<T>> extends Sort<T> {
    // BEWARE! Actual Heap below!

    private final class HeapNode {

        private SimpleEntry<T, Integer> value;
        private HeapNode parent, left, right;

        private HeapNode(SimpleEntry<T, Integer> value) {
            this.value = value;
            this.parent = this.left = this.right = null;
        }

    }

    @Override
    public List<SimpleEntry<T, Integer>> sort(List<SimpleEntry<T, Integer>> list) {
        if (list == null) { return null; }
        if (list.size() <= 1) { return list; }

        // 1. Build the structure and heapify
        HeapNode root = buildHeap(list);
        heapify(root);

        List<SimpleEntry<T, Integer>> sortedList = new ArrayList<>();
        int n = list.size();

        for (int i = 0; i < n; i++) {
            // The root always has the maximum value
            sortedList.add(root.value); // Add to the front to get Ascending order

            if (i < n - 1) {
                // Find the last leaf node in the current heap
                HeapNode lastLeaf = findLastLeaf(root, n - i);

                // Swap root value with last leaf value
                root.value = lastLeaf.value;

                // "Remove" the last leaf from the tree
                removeNode(lastLeaf);

                // Restore Max-Heap property from the root
                siftDown(root);
            }
        }

        // REVERT back to this return statement for testing,
        // Current return statement is for animation
        //return sortedList;

        for (int i = 0; i < sortedList.size(); i++) { list.set(i, sortedList.get(i)); }
        return list;
    }

    private HeapNode buildHeap(List<SimpleEntry<T, Integer>> list) {
        int n = list.size();

        List<HeapNode> temp = new ArrayList<>();

        int start = 0;
        int size = 1; // Level 0 has 1 node, Level 1 has 2, Level 2 has 4...
        HeapNode root = null;

        while (start < n) {
            int end = Math.min(start + size - 1, n - 1);
            List<HeapNode> next = new ArrayList<>();

            for (int i = start; i <= end; i++) {
                HeapNode newNode = new HeapNode(list.get(i));
                next.add(newNode);

                if (root == null) {
                    root = newNode; // The very first node created is our root
                } else {
                    // Determine which parent from the PREVIOUS level gets this child
                    // Each parent takes 2 children.
                    // Child index 0 & 1 go to Parent 0. Child index 2 & 3 go to Parent 1.
                    int childOffset = i - start;
                    int parentIndex = childOffset / 2;
                    HeapNode parent = temp.get(parentIndex);

                    newNode.parent = parent; // Set the back-pointer
                    if (childOffset % 2 == 0) {
                        parent.left = newNode;
                    } else {
                        parent.right = newNode;
                    }
                }
            }

            // Move to the next level: the children we just created become the parents
            temp = next;
            start = start + size; // Move start pointer
            size *= 2;            // Double the expected size for the next level
        }

        return root;
    }

    private void heapify(HeapNode root) {
        if (root == null) return;

        // 1. Get all nodes in a list using Level-Order Traversal (BFS)
        // We need this because we must heapify from bottom-to-top
        List<HeapNode> allNodes = new ArrayList<>();
        Queue<HeapNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            HeapNode curr = queue.poll();
            allNodes.add(curr);
            if (curr.left != null) queue.add(curr.left);
            if (curr.right != null) queue.add(curr.right);
        }

        // 2. Start from the last non-leaf node and move up to the root
        int n = allNodes.size();

        for (int i = (n / 2) - 1; i >= 0; i--) {
            siftDown(allNodes.get(i));
        }
    }

    private void siftDown(HeapNode node) {
        if (node == null) return;

        HeapNode smallest  = node;

        if (node.left != null && node.left.value.getKey().compareTo(smallest.value.getKey()) < 0) {
            smallest = node.left;
        }

        if (node.right != null && node.right.value.getKey().compareTo(smallest.value.getKey()) < 0) {
            smallest = node.right;
        }

        // If the largest is one of the children, swap values and continue sifting
        if (smallest != node) {
            // Swap only the SimpleEntry values, not the nodes themselves
            SimpleEntry<T, Integer> tempValue = node.value;
            node.value = smallest.value;
            smallest.value = tempValue;// Count the swap for visualization

            // Recurse down to the child we just swapped with
            siftDown(smallest);
        }
    }

    private HeapNode findLastLeaf(HeapNode root, int n) {
        String path = Integer.toBinaryString(n);
        HeapNode curr = root;
        // Skip the first '1' as it represents the root
        for (int i = 1; i < path.length(); i++) {
            if (path.charAt(i) == '0') curr = curr.left;
            else curr = curr.right;
        }
        return curr;
    }

    private void removeNode(HeapNode node) {
        if (node.parent != null) {
            if (node.parent.left == node) node.parent.left = null;
            else node.parent.right = null;
        }
    }

    @Override
    public String toString() {
        return "HeapSort (Using Heap Structure)";
    }
}
