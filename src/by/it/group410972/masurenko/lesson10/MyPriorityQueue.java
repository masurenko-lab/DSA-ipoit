package by.it.group410972.masurenko.lesson10;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

public class MyPriorityQueue<E> implements Queue<E> {

    private static final int DEFAULT_CAPACITY = 10;
    private Object[] heap;
    private int size;
    private final Comparator<? super E> comparator;

    @SuppressWarnings("unchecked")
    public MyPriorityQueue() {
        this.heap = new Object[DEFAULT_CAPACITY];
        this.size = 0;
        this.comparator = null;
    }

    @SuppressWarnings("unchecked")
    public MyPriorityQueue(Comparator<? super E> comparator) {
        this.heap = new Object[DEFAULT_CAPACITY];
        this.size = 0;
        this.comparator = comparator;
    }

    @SuppressWarnings("unchecked")
    public MyPriorityQueue(Collection<? extends E> c) {
        this();
        for (E element : c) {
            offer(element);
        }
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(heap[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            heap[i] = null;
        }
        size = 0;
    }

    @Override
    public boolean add(E element) {
        return offer(element);
    }

    @Override
    public E remove() {
        E result = poll();
        if (result == null) {
            throw new NoSuchElementException();
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object element) {
        for (int i = 0; i < size; i++) {
            if (element == null ? heap[i] == null : element.equals(heap[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean offer(E element) {
        if (element == null) {
            throw new NullPointerException();
        }

        if (size >= heap.length) {
            resize();
        }

        heap[size] = element;
        siftUp(size);
        size++;
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E poll() {
        if (size == 0) {
            return null;
        }

        E result = (E) heap[0];
        size--;

        if (size > 0) {
            heap[0] = heap[size];
            heap[size] = null;
            siftDown(0);
        } else {
            heap[0] = null;
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E peek() {
        return size == 0 ? null : (E) heap[0];
    }

    @Override
    public E element() {
        E result = peek();
        if (result == null) {
            throw new NoSuchElementException();
        }
        return result;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    @SuppressWarnings("unchecked")
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
        if (c == null) {
            throw new NullPointerException();
        }
        if (c == this) {
            throw new IllegalArgumentException();
        }

        boolean modified = false;
        for (E element : c) {
            if (offer(element)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean removeAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException();
        }
        boolean modified = false;
        int originalSize = size;
        Object[] tempHeap = new Object[heap.length];
        int newSize = 0;
        for (int i = 0; i < originalSize; i++) {
            if (!c.contains(heap[i])) {
                tempHeap[newSize++] = heap[i];
            } else {
                modified = true;
            }
        }
        heap = tempHeap;
        size = newSize;
        heapify();
        return modified;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean retainAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException();
        }
        boolean modified = false;
        int originalSize = size;
        Object[] tempHeap = new Object[heap.length];
        int newSize = 0;
        for (int i = 0; i < originalSize; i++) {
            if (c.contains(heap[i])) {
                tempHeap[newSize++] = heap[i];
            } else {
                modified = true;
            }
        }
        heap = tempHeap;
        size = newSize;
        heapify();
        return modified;
    }

    @SuppressWarnings("unchecked")
    private void siftUp(int index) {
        E element = (E) heap[index];
        while (index > 0) {
            int parentIndex = (index - 1) >>> 1;
            E parent = (E) heap[parentIndex];
            if (compare(element, parent) >= 0) {
                break;
            }
            heap[index] = parent;
            index = parentIndex;
        }
        heap[index] = element;
    }

    @SuppressWarnings("unchecked")
    private void siftDown(int index) {
        E element = (E) heap[index];
        int half = size >>> 1;
        while (index < half) {
            int childIndex = (index << 1) + 1;
            Object childObject = heap[childIndex];
            int rightIndex = childIndex + 1;
            if (rightIndex < size && compare((E) childObject, (E) heap[rightIndex]) > 0) {
                childIndex = rightIndex;
                childObject = heap[rightIndex];
            }
            if (compare(element, (E) childObject) <= 0) {
                break;
            }
            heap[index] = childObject;
            index = childIndex;
        }
        heap[index] = element;
    }

    private void heapify() {
        for (int i = (size >>> 1) - 1; i >= 0; i--) {
            siftDown(i);
        }
    }

    @SuppressWarnings("unchecked")
    private int compare(E a, E b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        } else {
            Comparable<? super E> comparable = (Comparable<? super E>) a;
            return comparable.compareTo(b);
        }
    }

    private void resize() {
        int newCapacity = heap.length * 2;
        Object[] newHeap = new Object[newCapacity];
        System.arraycopy(heap, 0, newHeap, 0, size);
        heap = newHeap;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }
}