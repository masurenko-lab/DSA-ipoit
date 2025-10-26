package by.it.group410972.masurenko.lesson13;

import java.util.*;

public class GraphA {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        Map<String, List<String>> graph = new HashMap<>();
        Set<String> allVertices = new HashSet<>();
        Map<String, Integer> inDegree = new HashMap<>();
        String[] edges = input.split(",\\s*");

        for (String edge : edges) {
            String[] parts = edge.split("\\s*->\\s*");
            String from = parts[0].trim();
            String to = parts[1].trim();
            allVertices.add(from);
            allVertices.add(to);
            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
            inDegree.putIfAbsent(from, 0);
            inDegree.put(to, inDegree.getOrDefault(to, 0) + 1);
        }
        for (String vertex : allVertices) {
            inDegree.putIfAbsent(vertex, 0);
        }
        List<String> sorted = topologicalSortKahn(graph, inDegree, allVertices);
        for (int i = 0; i < sorted.size(); i++) {
            if (i > 0) {
                System.out.print(" ");
            }
            System.out.print(sorted.get(i));
        }
        System.out.println();
    }
    private static List<String> topologicalSortKahn(Map<String, List<String>> graph, Map<String, Integer> inDegree, Set<String> allVertices) {
        List<String> result = new ArrayList<>();
        PriorityQueue<String> queue = new PriorityQueue<>();
        for (String vertex : allVertices) {
            if (inDegree.get(vertex) == 0) {
                queue.offer(vertex);
            }
        }
        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.add(current);
            if (graph.containsKey(current)) {
                List<String> neighbors = graph.get(current);
                Collections.sort(neighbors);
                for (String neighbor : neighbors) {
                    inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                    if (inDegree.get(neighbor) == 0) {
                        queue.offer(neighbor);
                    }
                }
            }
        }
        return result;
    }
}