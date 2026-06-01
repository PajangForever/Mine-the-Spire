package forever.pajang.minethespire.util;

import org.jspecify.annotations.NonNull;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.RandomAccess;

/**
 * A capacity-limited ordered list.
 *
 * <p>Elements are appended until the sequence reaches capacity. After that,
 * adding a new element evicts the oldest element at index {@code 0} and appends
 * the new element at the end.</p>
 */
public class Sequence<E> extends AbstractList<E> implements RandomAccess {
    private Object[] elements;
    private int size;


    public Sequence(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must not be negative.");
        }
        this.elements = new Object[capacity];
    }

    public int capacity() {
        return elements.length;
    }

    public List<E> setCapacity(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must not be negative.");
        }

        if (capacity == elements.length) {
            return List.of();
        }

        int newSize = Math.min(size, capacity);
        List<E> removed = new ArrayList<>(size - newSize);
        for (int i = newSize; i < size; i++) {
            removed.add(elementAt(i));
        }

        elements = Arrays.copyOf(elements, capacity);
        if (newSize < size) {
            Arrays.fill(elements, newSize, elements.length, null);
            size = newSize;
        }

        modCount++;
        return removed;
    }

    public Optional<E> addElement(@NonNull E element) {
        if (capacity() <= 0) return Optional.empty();

        Objects.requireNonNull(element, "element");

        if (size < elements.length) {
            elements[size++] = element;
            modCount++;
            return Optional.empty();
        }

        E removed = elementAt(0);
        System.arraycopy(elements, 1, elements, 0, elements.length - 1);
        elements[elements.length - 1] = element;
        modCount++;
        return Optional.of(removed);
    }

    public Optional<E> removeElement(E element) {
        if (capacity() <= 0) return Optional.empty();
        int index = indexOf(element);
        if (index == -1) {
            return Optional.empty();
        }

        return Optional.of(remove(index));
    }

    public Optional<E> removeFirstElement() {
        if (capacity() <= 0) return Optional.empty();
        if (size() == 0) {
            return Optional.empty();
        }
        return Optional.of(remove(0));
    }

    @Override
    public E get(int index) {
        checkElementIndex(index);
        return elementAt(index);
    }

    @Override
    public E set(int index, @NonNull E element) {
        Objects.requireNonNull(element, "element");
        checkElementIndex(index);

        E previous = elementAt(index);
        elements[index] = element;
        return previous;
    }

    @Override
    @Deprecated
    public boolean add(E element) {
        throw new UnsupportedOperationException("Use addElement(E element) instead.");
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException("Cannot insert element into a capacity-limited sequence.");
    }

    @Override
    public boolean remove(Object object) {
        if (size() == 0) return false;
        int index = indexOf(object);
        if (index == -1) {
            return false;
        }

        remove(index);
        return true;
    }

    @Override
    public E remove(int index) {
        if (size() == 0) return null;
        checkElementIndex(index);

        E removed = elementAt(index);
        int movedCount = size - index - 1;
        if (movedCount > 0) {
            System.arraycopy(elements, index + 1, elements, index, movedCount);
        }

        elements[--size] = null;
        modCount++;
        return removed;
    }

    @Override
    public void clear() {
        if (size == 0) {
            return;
        }

        Arrays.fill(elements, 0, size, null);
        size = 0;
        modCount++;
    }

    @Override
    public int size() {
        return size;
    }

    private void checkElementIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    @SuppressWarnings("unchecked")
    private E elementAt(int index) {
        return (E) elements[index];
    }
}
