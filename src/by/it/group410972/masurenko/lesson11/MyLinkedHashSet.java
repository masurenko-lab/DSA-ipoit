package by.it.group410972.masurenko.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyLinkedHashSet<E> implements Set<E> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private Node<E>[] table;
    private Node<E> head; //первый
    private Node<E> tail; //последний
    private int size;
    private final float loadFactor;

    private static class Node<E> {
        E element;
        Node<E> next;
        Node<E> before, after;

        Node(E element) {
            this.element = element;
        }
    }

    @SuppressWarnings("unchecked")
    public MyLinkedHashSet() {
        this.table = (Node<E>[]) new Node[DEFAULT_CAPACITY];
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.size = 0;
        this.head = null;
        this.tail = null;
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
        if (o == null) return false;

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

        Node<E> newNode = new Node<>(e);

        newNode.next = table[index];
        table[index] = newNode;
        linkNodeLast(newNode);
        size++;
        if (size > table.length * loadFactor) {
            resize();
        }
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) return false;
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
                unlinkNode(current);
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
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        Node<E> current = head;
        boolean first = true;

        while (current != null) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(current.element);
            first = false;
            current = current.after;
        }

        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object element : c) {
            if (!contains(element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E element : c) {
            if (add(element)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object element : c) {
            if (remove(element)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            E element = it.next();
            if (!c.contains(element)) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    private void linkNodeLast(Node<E> node) {
        if (tail == null) {
            //первый
            head = node;
            tail = node;
        } else {
            //в конец
            tail.after = node;
            node.before = tail;
            tail = node;
        }
    }

    private void unlinkNode(Node<E> node) {
        Node<E> before = node.before;
        Node<E> after = node.after;

        if (before == null) {
            head = after;
        } else {
            before.after = after;
            node.before = null;
        }

        if (after == null) {
            tail = before;
        } else {
            after.before = before;
            node.after = null;
        }
    }

    private int getIndex(Object o) {
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

        Node<E> current = head;
        head = null;
        tail = null;

        while (current != null) {
            Node<E> nextInOrder = current.after;
            current.before = null;
            current.after = null;
            int index = getIndex(current.element);
            current.next = table[index];
            table[index] = current;
            linkNodeLast(current);
            size++;
            current = nextInOrder;
        }
    }

    //Iterator для retainAll
    @Override
    public Iterator<E> iterator() {
        return new LinkedHashSetIterator();
    }

    private class LinkedHashSetIterator implements Iterator<E> {
        private Node<E> current = head;
        private Node<E> lastReturned = null;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            lastReturned = current;
            current = current.after;
            return lastReturned.element;
        }

        @Override
        public void remove() {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            MyLinkedHashSet.this.remove(lastReturned.element);
            lastReturned = null;
        }
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("toArray не реализован");
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("toArray не реализован");
    }
}