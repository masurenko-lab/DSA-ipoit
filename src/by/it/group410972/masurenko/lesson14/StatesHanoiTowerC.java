package by.it.group410972.masurenko.lesson14;

import java.util.*;

public class StatesHanoiTowerC {
    static class DSU {
        Map<String, String> parent = new HashMap<>();
        Map<String, Integer> size = new HashMap<>();

        void makeSet(String x) {
            parent.putIfAbsent(x, x);
            size.putIfAbsent(x, 1);
        }

        String find(String x) {
            if (!parent.get(x).equals(x))
                parent.put(x, find(parent.get(x))); // сжатие пути
            return parent.get(x);
        }
        void union(String a, String b) {
            String rootA = find(a);
            String rootB = find(b);
            if (rootA.equals(rootB)) return;
            if (size.get(rootA) < size.get(rootB)) {
                String tmp = rootA;
                rootA = rootB;
                rootB = tmp;
            }
            parent.put(rootB, rootA);
            size.put(rootA, size.get(rootA) + size.get(rootB));
        }
    }

    static void hanoi(int n, String from, String to, String aux, List<String> states, int[] heights) {
        if (n == 0) return;
        hanoi(n - 1, from, aux, to, states, heights);
        heights[from.charAt(0) - 'A']--;
        heights[to.charAt(0) - 'A']++;
        states.add(heights[0] + "-" + heights[1] + "-" + heights[2]);
        hanoi(n - 1, aux, to, from, states, heights);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int N = sc.nextInt();
        int[] heights = new int[]{N, 0, 0}; // A,B,C
        List<String> states = new ArrayList<>();
        hanoi(N, "A", "B", "C", states, heights);
        DSU dsu = new DSU();
        for (String state : states) {
            dsu.makeSet(state);
        }
        Map<Integer, List<String>> maxHeightMap = new HashMap<>();
        for (String state : states) {
            String[] parts = state.split("-");
            int maxH = Arrays.stream(parts).mapToInt(Integer::parseInt).max().getAsInt();
            maxHeightMap.computeIfAbsent(maxH, k -> new ArrayList<>()).add(state);
        }
        for (List<String> group : maxHeightMap.values()) {
            for (int i = 1; i < group.size(); i++) {
                dsu.union(group.get(0), group.get(i));
            }
        }
        Map<String, Integer> clusterSizes = new HashMap<>();
        for (String state : states) {
            String root = dsu.find(state);
            clusterSizes.put(root, clusterSizes.getOrDefault(root, 0) + 1);
        }
        List<Integer> sizes = new ArrayList<>(clusterSizes.values());
        Collections.sort(sizes);
        for (int i = 0; i < sizes.size(); i++) {
            if (i > 0) System.out.print(" ");
            System.out.print(sizes.get(i));
        }
        System.out.println();
    }
}