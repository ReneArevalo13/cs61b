package deque;


import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    /* construction of ArrayDeque data structure*/
    /* array of items*/
    private T[] items;
    /* integer of how many items in the array*/
    private int size;
    /* integer pointing to the element that is at the front of the array*/
    private int frontIndex;
    /* integer pointing to the element that is at the rear of the array*/
    private int rearIndex;
    /* length is how many spots are in the array */
    private int length;


    /**create empty ArrayDeque.**/
    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        frontIndex = -1;
        rearIndex = 0;
        this.length = items.length;
    }

    @Override
    public void addFirst(T item) {
        // check to see if resize needed, will get back to this
        if (isFull()) {
            resize(items.length * 2);
        }
        if (frontIndex == -1) {
            frontIndex = 0;
            rearIndex = 0;
        } else if (frontIndex == 0) {
            frontIndex = items.length - 1;
        } else {
            frontIndex--;
        }
        items[frontIndex] = item;
        size++;
    }
    @Override
    public void addLast(T item) {
        if (isFull()) {
            resize(items.length * 2);
        }
        if (frontIndex == -1) {
            frontIndex = 0;
            rearIndex = 0;
        } else if (rearIndex == items.length - 1) {
            rearIndex = 0;
        } else {
            rearIndex++;
        }
        items[rearIndex] = item;
        size++;
    }
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        if (size < items.length / 4 && items.length > 8) {
            resize(items.length / 2);
            frontIndex = 0;
        }
        T value = items[frontIndex];
        items[frontIndex] = null;
        // when only one item in array, front==rear
        if (frontIndex == rearIndex) {
            frontIndex = -1;
            rearIndex = -1;
        /* when front points to last index in the array
        need to change front back to 0.
         */
        } else if (frontIndex == items.length - 1) {
            frontIndex = 0;
        } else  {
            frontIndex++;
        }
        size--;
        return value;
    }
    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        if (size < items.length / 4 && items.length > 8) {
            resize(items.length / 2);
        }
        T value = items[rearIndex];
        items[rearIndex] = null;
        if (frontIndex == rearIndex) {
            frontIndex = -1;
            rearIndex = -1;
            /* if rear points at first element
            change it to point at length-1 spot
             */
        } else if (rearIndex == 0) {
            rearIndex = items.length - 1;
        } else {
            rearIndex--;
        }
        size--;
        return value;
    }
    @Override
    public T get(int index) {
        if (index < 0 || index >= size || isEmpty()) {
            return null;
        }
        index = frontIndex + index;
        index = index % items.length;
        return items[index];
    }
    @Override
    public int size() {
        return size;
    }
    private boolean isFull() {
        return size == items.length;
    }
    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(get(i) + " ");
        }
        System.out.println();
    }
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }
    private class ArrayDequeIterator implements Iterator<T> {
        private int wizPos;
        ArrayDequeIterator() {
            wizPos = 0;
        }
        public boolean hasNext() {
            return wizPos < size;
        }

        public T next() {
            T returnItem = get(wizPos);
            wizPos += 1;
            return returnItem;
        }
    }
    @Override
    public boolean equals(Object other) {

        /* Returns whether the parameter o is equal to the Deque.
        o is considered equal if it is a Deque and if it contains the same contents
        (as governed by the generic T’s equals method) in the same order
         */

        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }

        if (!(other instanceof Deque)) {
            return false;
        }
        Deque<T> o = (Deque<T>) other;
        if (o.size() != this.size()) {
            return false;
        }
        for (int i = 0; i < this.size; i++) {
            if (!o.get(i).equals(this.get(i))) {
                return false;
            }
        }
        return true;
    }

    private void resize(int capacity) {
        T[] temp = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            temp[i] = items[(frontIndex + i) % items.length];
        }
        items = temp;
        frontIndex = 0;
        rearIndex = size - 1;
    }

//





}
