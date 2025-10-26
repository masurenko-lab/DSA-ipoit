package by.it.group410972.masurenko.lesson12;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;

public class MySplayMap implements NavigableMap<Integer, String> {

    private static class Node {
        Integer key;
        String value;
        Node left;
        Node right;
        Node parent;

        Node(Integer key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node root;
    private int size;

    public MySplayMap() {
        root = null;
        size = 0;
    }

    @Override
    public String toString() {
        if (root == null) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        inOrderTraversal(root, sb);
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2); // Remove last ", "
        }
        sb.append("}");
        return sb.toString();
    }

    private void inOrderTraversal(Node node, StringBuilder sb) {
        if (node != null) {
            inOrderTraversal(node.left, sb);
            sb.append(node.key).append("=").append(node.value).append(", ");
            inOrderTraversal(node.right, sb);
        }
    }

    @Override
    public String put(Integer key, String value) {
        if (key == null) {
            throw new NullPointerException("Ключ должен быть не нулевой");
        }

        Node node = findNode(key);
        if (node != null) {
            String oldValue = node.value;
            node.value = value;
            splay(node);
            return oldValue;
        }
        root = insert(root, key, value, null);
        node = findNode(key);
        if (node != null) {
            splay(node);
        }
        size++;
        return null;
    }

    private Node insert(Node node, Integer key, String value, Node parent) {
        if (node == null) {
            Node newNode = new Node(key, value);
            newNode.parent = parent;
            return newNode;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insert(node.left, key, value, node);
        }
        else if (cmp > 0) {
            node.right = insert(node.right, key, value, node);
        }
        return node;
    }

    @Override
    public String remove(Object key) {
        if (!(key instanceof Integer)) {
            return null;
        }
        Node node = findNode((Integer) key);
        if (node == null) {
            return null;
        }
        String removedValue = node.value;
        splay(node);

        // Now remove the root
        if (root.left == null) {
            root = root.right;
            if (root != null) {
                root.parent = null;
            }
        }
        else
        {
            Node rightSubtree = root.right;
            root = root.left;
            root.parent = null;
            Node maxLeft = root;
            while (maxLeft.right != null) {
                maxLeft = maxLeft.right;
            }
            splay(maxLeft);
            maxLeft.right = rightSubtree;
            if (rightSubtree != null) {
                rightSubtree.parent = maxLeft;
            }
        }
        size--;
        return removedValue;
    }

    @Override
    public String get(Object key) {
        if (!(key instanceof Integer)) {
            return null;
        }
        Node node = findNode((Integer) key);
        if (node != null) {
            splay(node);
            return node.value;
        }
        return null;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        if (!(value instanceof String)) {
            return false;
        }
        return containsValue(root, (String) value);
    }

    private boolean containsValue(Node node, String value) {
        if (node == null) {
            return false;
        }
        if (value.equals(node.value)) {
            return true;
        }
        return containsValue(node.left, value) || containsValue(node.right, value);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Integer lowerKey(Integer key) {
        Node node = lowerNode(key);
        return node != null ? node.key : null;
    }

    private Node lowerNode(Integer key) {
        Node current = root;
        Node candidate = null;

        while (current != null) {
            if (current.key.compareTo(key) < 0) {
                candidate = current;
                current = current.right;
            } else {
                current = current.left;
            }
        }
        return candidate;
    }

    @Override
    public Integer floorKey(Integer key) {
        Node node = floorNode(key);
        return node != null ? node.key : null;
    }

    private Node floorNode(Integer key) {
        Node current = root;
        Node candidate = null;

        while (current != null) {
            int cmp = current.key.compareTo(key);
            if (cmp <= 0) {
                candidate = current;
                if (cmp == 0) {
                    break;
                }
                current = current.right;
            } else {
                current = current.left;
            }
        }
        return candidate;
    }

    @Override
    public Integer ceilingKey(Integer key) {
        Node node = ceilingNode(key);
        return node != null ? node.key : null;
    }

    private Node ceilingNode(Integer key) {
        Node current = root;
        Node candidate = null;

        while (current != null) {
            int cmp = current.key.compareTo(key);
            if (cmp >= 0) {
                candidate = current;
                if (cmp == 0) {
                    break;
                }
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return candidate;
    }

    @Override
    public Integer higherKey(Integer key) {
        Node node = higherNode(key);
        return node != null ? node.key : null;
    }

    private Node higherNode(Integer key) {
        Node current = root;
        Node candidate = null;

        while (current != null) {
            if (current.key.compareTo(key) > 0) {
                candidate = current;
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return candidate;
    }

    @Override
    public NavigableMap<Integer, String> headMap(Integer toKey) {
        return headMap(toKey, false);
    }

    @Override
    public NavigableMap<Integer, String> headMap(Integer toKey, boolean inclusive) {
        MySplayMap result = new MySplayMap();
        headMap(root, toKey, inclusive, result);
        return result;
    }

    private void headMap(Node node, Integer toKey, boolean inclusive, MySplayMap result) {
        if (node == null) {
            return;
        }
        headMap(node.left, toKey, inclusive, result);
        if ((inclusive && node.key.compareTo(toKey) <= 0) ||
                (!inclusive && node.key.compareTo(toKey) < 0)) {
            result.put(node.key, node.value);
        }
        if (node.key.compareTo(toKey) < 0) {
            headMap(node.right, toKey, inclusive, result);
        }
    }

    @Override
    public NavigableMap<Integer, String> tailMap(Integer fromKey) {
        return tailMap(fromKey, true);
    }

    @Override
    public NavigableMap<Integer, String> tailMap(Integer fromKey, boolean inclusive) {
        MySplayMap result = new MySplayMap();
        tailMap(root, fromKey, inclusive, result);
        return result;
    }

    private void tailMap(Node node, Integer fromKey, boolean inclusive, MySplayMap result) {
        if (node == null) {
            return;
        }
        tailMap(node.right, fromKey, inclusive, result);
        if ((inclusive && node.key.compareTo(fromKey) >= 0) ||
                (!inclusive && node.key.compareTo(fromKey) > 0)) {
            result.put(node.key, node.value);
        }
        if (node.key.compareTo(fromKey) > 0) {
            tailMap(node.left, fromKey, inclusive, result);
        }
    }

    @Override
    public Integer firstKey() {
        if (root == null) {
            throw new java.util.NoSuchElementException();
        }
        Node node = root;
        while (node.left != null) {
            node = node.left;
        }
        return node.key;
    }

    @Override
    public Integer lastKey() {
        if (root == null) {
            throw new java.util.NoSuchElementException();
        }
        Node node = root;
        while (node.right != null) {
            node = node.right;
        }
        return node.key;
    }

    private Node findNode(Integer key) {
        Node current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                return current;
            }
        }
        return null;
    }

    private void splay(Node node) {
        while (node != root) {
            if (node.parent == root) {
                if (node == root.left) {
                    rotateRight(root);
                } else {
                    rotateLeft(root);
                }
            } else {
                Node parent = node.parent;
                Node grandparent = parent.parent;

                if (node == parent.left && parent == grandparent.left) {
                    rotateRight(grandparent);
                    rotateRight(parent);
                } else if (node == parent.right && parent == grandparent.right) {
                    rotateLeft(grandparent);
                    rotateLeft(parent);
                } else if (node == parent.right && parent == grandparent.left) {
                    rotateLeft(parent);
                    rotateRight(grandparent);
                } else {
                    rotateRight(parent);
                    rotateLeft(grandparent);
                }
            }
        }
    }

    private void rotateLeft(Node x) {
        Node y = x.right;
        if (y == null) return;

        x.right = y.left;
        if (y.left != null) {
            y.left.parent = x;
        }

        y.parent = x.parent;
        if (x.parent == null) {
            root = y;
        }
        else if (x == x.parent.left) {
            x.parent.left = y;
        }
        else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    private void rotateRight(Node y) {
        Node x = y.left;
        if (x == null) return;
        y.left = x.right;
        if (x.right != null) {
            x.right.parent = y;
        }
        x.parent = y.parent;
        if (y.parent == null) {
            root = x;
        }
        else if (y == y.parent.left) {
            y.parent.left = x;
        }
        else {
            y.parent.right = x;
        }
        x.right = y;
        y.parent = x;
    }

    @Override
    public Entry<Integer, String> lowerEntry(Integer key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Integer, String> floorEntry(Integer key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Integer, String> ceilingEntry(Integer key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Integer, String> higherEntry(Integer key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Integer, String> firstEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Integer, String> lastEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Integer, String> pollFirstEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Integer, String> pollLastEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap<Integer, String> descendingMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableSet<Integer> navigableKeySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableSet<Integer> descendingKeySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap<Integer, String> subMap(Integer fromKey, boolean fromInclusive, Integer toKey, boolean toInclusive) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Comparator<? super Integer> comparator() {
        return null;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public java.util.Set<Integer> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public java.util.Collection<String> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public java.util.Set<Entry<Integer, String>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }
}
