package by.it.group410972.masurenko.lesson13;

import java.util.*;

public class GraphC {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        Map<String, List<String>> graph = new HashMap<>();
        Map<String, List<String>> reverseGraph = new HashMap<>();
        Set<String> allVertices = new HashSet<>();
        String[] edges = input.split(",\\s*");

        for (String edge : edges) {
            String[] parts = edge.split("\\s*->\\s*");
            String from = parts[0].trim();
            String to = parts[1].trim();
            allVertices.add(from);
            allVertices.add(to);
            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
            reverseGraph.computeIfAbsent(to, k -> new ArrayList<>()).add(from);
        }
        List<List<String>> scc = findStronglyConnectedComponents(graph, reverseGraph, allVertices);

        for (List<String> component : scc) {
            Collections.sort(component);
            StringBuilder sb = new StringBuilder();
            for (String vertex : component) {
                sb.append(vertex);
            }
            System.out.println(sb.toString());
        }
    }

    private static List<List<String>> findStronglyConnectedComponents(
            Map<String, List<String>> graph,
            Map<String, List<String>> reverseGraph,
            Set<String> allVertices) {

        List<String> order = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        for (String vertex : allVertices) {
            if (!visited.contains(vertex)) {
                dfs1(vertex, graph, visited, order);
            }
        }
        Collections.reverse(order);
        visited.clear();
        List<List<String>> components = new ArrayList<>();
        for (String vertex : order) {
            if (!visited.contains(vertex)) {
                List<String> component = new ArrayList<>();
                dfs2(vertex, reverseGraph, visited, component);
                components.add(component);
            }
        }
        return components;
    }

    private static void dfs1(String vertex, Map<String, List<String>> graph, Set<String> visited, List<String> order)
    {
        visited.add(vertex);
        if (graph.containsKey(vertex)) {
            for (String neighbor : graph.get(vertex)) {
                if (!visited.contains(neighbor)) {
                    dfs1(neighbor, graph, visited, order);
                }
            }
        }
        order.add(vertex);
    }

    private static void dfs2(String vertex, Map<String, List<String>> reverseGraph,
                             Set<String> visited, List<String> component)
    {
        visited.add(vertex); component.add(vertex);
        if (reverseGraph.containsKey(vertex)) {
            for (String neighbor : reverseGraph.get(vertex)) {
                if (!visited.contains(neighbor)) {
                    dfs2(neighbor, reverseGraph, visited, component);
                }
            }
        }
    }
}