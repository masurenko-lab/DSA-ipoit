package by.it.group410972.masurenko.lesson13;

import java.util.*;

public class GraphB {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        Map<String, List<String>> graph = new HashMap<>();
        Set<String> allVertices = new HashSet<>();
        String[] edges = input.split(",\\s*");

        for (String edge : edges) {
            String[] parts = edge.split("\\s*->\\s*");
            String from = parts[0].trim();
            String to = parts[1].trim();
            allVertices.add(from);
            allVertices.add(to);
            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
        }
        boolean hasCycle = hasCycle(graph, allVertices);
        System.out.println(hasCycle ? "yes" : "no");
    }

    private static boolean hasCycle(Map<String, List<String>> graph, Set<String> allVertices) {
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();

        for (String vertex : allVertices) {
            if (!visited.contains(vertex)) {
                if (dfs(vertex, graph, visited, recursionStack)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean dfs(String vertex, Map<String, List<String>> graph,
                               Set<String> visited, Set<String> recursionStack) {
        if (recursionStack.contains(vertex)) {
            return true;
        }
        if (visited.contains(vertex)) {
            return false;
        }
        visited.add(vertex);
        recursionStack.add(vertex);
        if (graph.containsKey(vertex)) {
            for (String neighbor : graph.get(vertex)) {
                if (dfs(neighbor, graph, visited, recursionStack)) {
                    return true;
                }
            }
        }
        recursionStack.remove(vertex);
        return false;
    }
}