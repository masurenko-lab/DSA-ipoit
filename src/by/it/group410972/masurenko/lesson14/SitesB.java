package by.it.group410972.masurenko.lesson14;

import java.util.*;
import java.util.stream.Collectors;

public class SitesB {

    static class DSU {
        private Map<String, String> parent;
        private Map<String, Integer> rank;
        private Map<String, Integer> size;

        public DSU() {
            parent = new HashMap<>();
            rank = new HashMap<>();
            size = new HashMap<>();
        }

        public void makeSet(String x) {
            if (!parent.containsKey(x)) {
                parent.put(x, x);
                rank.put(x, 0);
                size.put(x, 1);
            }
        }

        public String find(String x) {
            if (!parent.get(x).equals(x)) {
                // Сокращение пути (path compression)
                parent.put(x, find(parent.get(x)));
            }
            return parent.get(x);
        }

        public void union(String x, String y) {
            String rootX = find(x);
            String rootY = find(y);
            if (rootX.equals(rootY)) return;
            if (rank.get(rootX) < rank.get(rootY)) {
                parent.put(rootX, rootY);
                size.put(rootY, size.get(rootY) + size.get(rootX));
            } else if (rank.get(rootX) > rank.get(rootY)) {
                parent.put(rootY, rootX);
                size.put(rootX, size.get(rootX) + size.get(rootY));
            } else {
                parent.put(rootY, rootX);
                size.put(rootX, size.get(rootX) + size.get(rootY));
                rank.put(rootX, rank.get(rootX) + 1);
            }
        }

        public int getSize(String x) {
            return size.get(find(x));
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DSU dsu = new DSU();
        Set<String> allSites = new HashSet<>();

        while (true) {
            String line = scanner.nextLine();
            if (line.equals("end")) {
                break;
            }

            String[] sites = line.split("\\+");
            if (sites.length != 2) {
                continue;
            }

            String site1 = sites[0].trim();
            String site2 = sites[1].trim();

            allSites.add(site1);
            allSites.add(site2);

            dsu.makeSet(site1);
            dsu.makeSet(site2);
            dsu.union(site1, site2);
        }
        Map<String, Integer> clusterSizes = new HashMap<>();
        for (String site : allSites) {
            String root = dsu.find(site);
            clusterSizes.put(root, dsu.getSize(root));
        }
        List<Integer> sizes = new ArrayList<>(clusterSizes.values());
        Collections.sort(sizes, Collections.reverseOrder());
        String result = sizes.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" "));

        System.out.println(result);

        scanner.close();
    }
}