package by.it.group410972.masurenko.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyHashSet<E> implements Set<E> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private Node<E>[] table;
    private int size;
    private final float loadFactor;

    private static class Node<E> {
        E element;
        Node<E> next;

        Node(E element, Node<E> next) {
            this.element = element;
            this.next = next;
        }
    }

    @SuppressWarnings("unchecked")
    public MyHashSet() {
        this.table = (Node<E>[]) new Node[DEFAULT_CAPACITY];
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        int index = getIndex(o);
        Node<E> current = table[index];

        while (current != null) {
            if (objectsEqual(o, current.element)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public boolean add(E e) {
        if (e == null) {
            throw new NullPointerException("Нулевые элементы не поддерживаются");
        }
        int index = getIndex(e);
        Node<E> current = table[index];
        while (current != null) {
            if (objectsEqual(e, current.element)) {
                return false;
            }
            current = current.next;
        }
        table[index] = new Node<>(e, table[index]);
        size++;

        if (size > table.length * loadFactor) {
            resize();
        }
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int index = getIndex(o);
        Node<E> current = table[index];
        Node<E> prev = null;

        while (current != null) {
            if (objectsEqual(o, current.element)) {
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }
    @Override
    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        size = 0;
    }
    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Node<E> node : table) {
            Node<E> current = node;
            while (current != null) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(current.element);
                first = false;
                current = current.next;
            }
        }
        sb.append("]");
        return sb.toString();
    }
    private int getIndex(Object o) {
        if (o == null) {
            return 0;
        }
        int hashCode = o.hashCode();
        return (hashCode & 0x7FFFFFFF) % table.length;
    }
    private boolean objectsEqual(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }
    @SuppressWarnings("unchecked")
    private void resize() {
        Node<E>[] oldTable = table;
        table = (Node<E>[]) new Node[oldTable.length * 2];
        size = 0;
        for (Node<E> node : oldTable) {
            Node<E> current = node;
            while (current != null) {
                int newIndex = getIndex(current.element);
                table[newIndex] = new Node<>(current.element, table[newIndex]);
                size++;
                current = current.next;
            }
        }
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException("Итератор не реализован");
    }
    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("toArray не реализован");
    }
    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("toArray не реализован");
    }
    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("containsAll не реализован");
    }
    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("addAll не реализован");
    }
    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("retainAll не реализован");
    }
    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("removeAll не реализован");
    }
}