package by.it.group410972.masurenko.lesson12;

import java.util.Map;

public class MyAvlMap implements Map<Integer, String> {

    private static class Node {
        Integer key;
        String value;
        Node left;
        Node right;
        int height;

        Node(Integer key, String value) {
            this.key = key;
            this.value = value;
            this.height = 1;
        }
    }

    private Node root;
    private int size;

    public MyAvlMap() {
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
            sb.setLength(sb.length() - 2);
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

        String[] oldValue = new String[1];
        root = put(root, key, value, oldValue);
        if (oldValue[0] == null) {
            size++;
        }
        return oldValue[0];
    }

    private Node put(Node node, Integer key, String value, String[] oldValue) {
        if (node == null) {
            return new Node(key, value);
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = put(node.left, key, value, oldValue);
        } else if (cmp > 0) {
            node.right = put(node.right, key, value, oldValue);
        } else {
            oldValue[0] = node.value;
            node.value = value;
            return node;
        }

        updateHeight(node);
        return balance(node);
    }

    @Override
    public String remove(Object key) {
        if (!(key instanceof Integer)) {
            return null;
        }

        String[] removedValue = new String[1];
        root = remove(root, (Integer) key, removedValue);
        if (removedValue[0] != null) {
            size--;
        }
        return removedValue[0];
    }

    private Node remove(Node node, Integer key, String[] removedValue) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = remove(node.left, key, removedValue);
        } else if (cmp > 0) {
            node.right = remove(node.right, key, removedValue);
        } else {
            removedValue[0] = node.value;

            if (node.left == null && node.right == null) {
                return null;
            } else if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            } else {
                Node successor = findMin(node.right);
                node.key = successor.key;
                node.value = successor.value;
                node.right = remove(node.right, successor.key, new String[1]);
            }
        }

        updateHeight(node);
        return balance(node);
    }

    private Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    @Override
    public String get(Object key) {
        if (!(key instanceof Integer)) {
            return null;
        }
        return get(root, (Integer) key);
    }

    private String get(Node node, Integer key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return get(node.left, key);
        } else if (cmp > 0) {
            return get(node.right, key);
        } else {
            return node.value;
        }
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
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

    private int height(Node node) {
        return node == null ? 0 : node.height;
    }

    private void updateHeight(Node node) {
        node.height = Math.max(height(node.left), height(node.right)) + 1;
    }

    private int balanceFactor(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private Node balance(Node node) {
        int balanceFactor = balanceFactor(node);
        if (balanceFactor > 1) {
            if (balanceFactor(node.left) < 0) {
                node.left = rotateLeft(node.left);
            }
            return rotateRight(node);
        }
        if (balanceFactor < -1) {
            if (balanceFactor(node.right) > 0) {
                node.right = rotateRight(node.right);
            }
            return rotateLeft(node);
        }
        return node;
    }

    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;
        x.right = y; y.left = T2;
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;
        y.left = x; x.right = T2;
        updateHeight(x);
        updateHeight(y);
        return y;
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
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
}