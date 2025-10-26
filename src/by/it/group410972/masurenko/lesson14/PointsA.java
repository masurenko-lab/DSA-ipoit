package by.it.group410972.masurenko.lesson14;

import java.util.*;
import java.util.stream.Collectors;

public class PointsA {

    static class DSU {
        private int[] parent;
        private int[] rank;
        private int[] size;

        public DSU(int n) {
            parent = new int[n];
            rank = new int[n];
            size = new int[n];

            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX == rootY) return;

            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
                size[rootY] += size[rootX];
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
                size[rootX] += size[rootY];
            } else {
                parent[rootY] = rootX;
                size[rootX] += size[rootY];
                rank[rootX]++;
            }
        }

        public int getSize(int x) {
            return size[find(x)];
        }
    }

    static class Point {
        int x, y, z;
        int index;

        public Point(int x, int y, int z, int index) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.index = index;
        }

        public double distanceTo(Point other) {
            int dx = x - other.x;
            int dy = y - other.y;
            int dz = z - other.z;
            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int maxDistance = scanner.nextInt();
        int n = scanner.nextInt();

        List<Point> points = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            int z = scanner.nextInt();
            points.add(new Point(x, y, z, i));
        }

        DSU dsu = new DSU(n);

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                Point p1 = points.get(i);
                Point p2 = points.get(j);
                double distance = p1.distanceTo(p2);

                if (distance < maxDistance) {
                    dsu.union(i, j);
                }
            }
        }
        Set<Integer> roots = new HashSet<>();
        for (int i = 0; i < n; i++) {
            roots.add(dsu.find(i));
        }

        List<Integer> sizes = new ArrayList<>();
        for (int root : roots) {
            sizes.add(dsu.getSize(root));
        }
        Collections.sort(sizes, Collections.reverseOrder());

        String result = sizes.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" "));

        System.out.println(result);
        scanner.close();
    }
}