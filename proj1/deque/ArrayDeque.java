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
        frontIndex = -1;
        rearIndex = 0;
        this.length = items.length;
    }

    public void addFirst(T item){
        // check to see if resize needed, will get back to this
        if (isFull()){
            resize(items.length * 2);
        }
        if (frontIndex == -1){
            frontIndex = 0;
            rearIndex = 0;
        } else if (frontIndex == 0) {
            frontIndex = items.length - 1;
        } else{
            frontIndex--;
        }
        items[frontIndex] = item;
        size++;
    }

    public void addLast(T item){
        if (isFull()){
            resize(items.length * 2);
        }
        if (frontIndex == -1){
            frontIndex = 0;
            rearIndex = 0;
        } else if (rearIndex == items.length - 1) {
            rearIndex = 0;
        } else{
            rearIndex++;
        }
        items[rearIndex] = item;
        size++;
    }
    public T removeFirst(){
        if (isEmpty()){
            return null;
        }
        if (size < items.length / 4 && items.length > 8){
            resize(items.length / 2);
        }
        T value = items[frontIndex];
        items[frontIndex] = null;
        // when only one item in array, front==rear
        if (frontIndex == rearIndex){
            frontIndex = -1;
            rearIndex = -1;
        /* when front points to last index in the array
        need to change front back to 0.
         */
        } else if (frontIndex == items.length - 1) {
            frontIndex = 0;
        } else{
            frontIndex++;
        }
        size--;
        return value;
    }
    public T removeLast(){
        if (isEmpty()){
            return null;
        }
        if (size < items.length / 4 && items.length > 8){
            resize(items.length / 2);
        }
        T value = items[rearIndex];
        items[rearIndex] = null;
        if (frontIndex == rearIndex){
            frontIndex = -1;
            rearIndex = -1;
            /* if rear points at first element
            change it to point at length-1 spot
             */
        } else if (rearIndex == 0) {
            rearIndex = items.length -1;
        } else{
            rearIndex--;
        }
        size--;
        return value;
    }
    public T get(int index) {
        if (index < 0 || index >= size || isEmpty()) {
            return null;
        }
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
//    private boolean underUsageFactor(){
//
//    }
    private void resize(int capactiy) {
        T[] temp = (T[]) new Object[capactiy];
        int endPos = temp.length / 4;
        System.arraycopy(items, 0, temp, endPos, size);
        items = temp;
        frontIndex = -1;
        rearIndex = 0;
    }

    public static void main(String[] args) {

        ArrayDeque<Integer> A = new ArrayDeque();

        A.addFirst(12);
        A.addLast(3);
        A.addFirst(14);
//        int rmvF = A.removeFirst();
        A.addLast(91);
        A.addFirst(8);
        A.addLast(11);
        A.addFirst(4);
        A.addLast(63);
//        int rmvL = A.removeLast();
//        int get1 = A.get(1);
        A.resize(A.size * 2);
        A.addLast(2);
        A.addFirst(523);
        A.addFirst(3);
        A.addFirst(31);
        A.addLast(78);
        A.addLast(98);
        A.addFirst(11);
        A.addLast(37);
        A.resize(A.size * 2);
    }







}