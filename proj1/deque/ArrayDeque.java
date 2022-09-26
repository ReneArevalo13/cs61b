package deque;

public class ArrayDeque<T> {
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
        frontIndex = 0;
        rearIndex = -1;
        length = items.length;
    }

    public void addLast(T item) {
        /*Check to see if resize needed, will get back to this.*/
        if (rearIndex == -1) {
            rearIndex = 0;
            frontIndex = 0;
        } else if (rearIndex == 0) {
            rearIndex = length - 1;
        } else {
            rearIndex--;
        }
        items[rearIndex] = item;
        size++;
    }

    public void addFirst(T item) {
        if (rearIndex == -1) {
            frontIndex = 0;
            rearIndex = 0;
        } else if (frontIndex == length - 1) {
            frontIndex = 0;
        } else {
            frontIndex++;
        }
        items[frontIndex] = item;
        size++;
    }
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T value = items[frontIndex];
        items[frontIndex] = null;
        /* When only one item in array.*/
        if (frontIndex == rearIndex) {
            frontIndex = -1;
            rearIndex = -1;
        /* When front points to last item in the array
        need to change front back to 0. */
        } else if (rearIndex == length - 1) {
            rearIndex = 0;
        } else {
            rearIndex++;
        }
        size--;

        return value;
    }
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T value = items[frontIndex];
        items[frontIndex] = null;
        if (frontIndex == rearIndex) {
            frontIndex = -1;
            rearIndex = -1;
            /*If rear points at first element
            change it to point at length-1 spot.*/
        } else if (frontIndex == 0) {
            frontIndex = length - 1;
        } else {
            frontIndex--;
        }
        size--;
        return value;
    }
    public T get(int index) {
//        if (index < 0 || index >= size || isEmpty()) {
//            return null;
//        }
        index = frontIndex + index;
        index = index % items.length;
        return items[index];
    }
    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
    public boolean isFull() {
        return size == length;
    }
    public T getLast() {
        return items[rearIndex];
    }
    public T getFirst() {
        return items[frontIndex];
    }

    public static void main(String[] args) {

        ArrayDeque<Integer> A = new ArrayDeque();
        A.addFirst(12);

        A.addLast(3);
        A.addFirst(14);
        //int rmvF = A.removeFirst();
        A.addLast(91);
        A.addFirst(8);
        A.addLast(11);
        A.addFirst(4);
        A.addLast(63);
        //int rmvL = A.removeLast();
        int get1 = A.get(1);

    }







}
