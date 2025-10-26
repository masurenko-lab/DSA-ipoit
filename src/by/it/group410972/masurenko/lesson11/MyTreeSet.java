package by.it.group410972.masurenko.lesson11;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public class MyTreeSet<E> implements Set<E> {
    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elements;
    private int size;
    private final Comparator<? super E> comparator;

    @SuppressWarnings("unchecked")
    public MyTreeSet() {
        this.elements = new Object[DEFAULT_CAPACITY];
        this.size = 0;
        this.comparator = null; // natural ordering
    }

    @SuppressWarnings("unchecked")
    public MyTreeSet(Comparator<? super E> comparator) {
        this.elements = new Object[DEFAULT_CAPACITY];
        this.size = 0;
        this.comparator = comparator;
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
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        return binarySearch((E) o) >= 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean add(E e) {
        if (e == null) {
            throw new NullPointerException("Нулевые элементы не поддерживаются");
        }

        int index = binarySearch(e);
        if (index >= 0) {
            return false;
        }
        int insertIndex = -index - 1;
        if (size == elements.length) {
            ensureCapacity();
        }
        System.arraycopy(elements, insertIndex, elements, insertIndex + 1, size - insertIndex);
        elements[insertIndex] = e;
        size++;
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {
        int index = binarySearch((E) o);
        if (index < 0) {
            return false;
        }
        System.arraycopy(elements, index + 1, elements, index, size - index - 1);
        elements[--size] = null;

        return true;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(elements[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    //дополнительные методы
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

    @SuppressWarnings("unchecked")
    private int binarySearch(E key) {
        int low = 0;
        int high = size - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            E midVal = (E) elements[mid];
            int cmp = compare(midVal, key);
            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -(low + 1);
    }

    @SuppressWarnings("unchecked")
    private int compare(E a, E b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        } else {
            return ((Comparable<? super E>) a).compareTo(b);
        }
    }

    private void ensureCapacity() {
        int newCapacity = elements.length * 2;
        Object[] newElements = new Object[newCapacity];
        System.arraycopy(elements, 0, newElements, 0, size);
        elements = newElements;
    }

    @Override
    public Iterator<E> iterator() {
        return new TreeSetIterator();
    }

    private class TreeSetIterator implements Iterator<E> {
        private int currentIndex = 0;
        private int lastReturnedIndex = -1;

        @Override
        public boolean hasNext() {
            return currentIndex < size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            lastReturnedIndex = currentIndex;
            return (E) elements[currentIndex++];
        }

        @Override
        public void remove() {
            if (lastReturnedIndex == -1) {
                throw new IllegalStateException();
            }
            MyTreeSet.this.remove(elements[lastReturnedIndex]);
            currentIndex--;
            lastReturnedIndex = -1;
        }
    }

    @SuppressWarnings("unchecked")
    public E first() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }
        return (E) elements[0];
    }

    @SuppressWarnings("unchecked")
    public E last() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }
        return (E) elements[size - 1];
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        System.arraycopy(elements, 0, result, 0, size);
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            return (T[]) toArray();
        }
        System.arraycopy(elements, 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }
}