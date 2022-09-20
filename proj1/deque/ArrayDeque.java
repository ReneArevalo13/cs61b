package deque;

public class ArrayDeque<Type> {
    private Type[] items;
    private int size;
    private int frontIndex;
    private int rearIndex;
    // length is how many spots are in the array
    private int length;

    /**create empty ArrayDeque**/
    public ArrayDeque(){
        items = (Type[]) new Object[8];
        size = 0;
        frontIndex = -1;
        rearIndex = 0;
        length = items.length;
    }

    public void addFirst(Type item){
        // check to see if resize needed, will get back to this
        if (frontIndex == -1){
            frontIndex = 0;
            rearIndex = 0;
        } else if (frontIndex == 0) {
            frontIndex = length - 1;
        } else{
            frontIndex--;
        }
        items[frontIndex] = item;
        size++;
    }

    public void addLast(Type item){
        if (frontIndex == -1){
            frontIndex = 0;
            rearIndex = 0;
        } else if (rearIndex == length - 1) {
            rearIndex = 0;
        } else{
            rearIndex++;
        }
        items[rearIndex] = item;
        size++;
    }
    public Type removeFirst(){
        if (isEmpty()){
            return null;
        }
        Type value = items[frontIndex];
        // when only one item in array, front==rear
        if (frontIndex == rearIndex){
            frontIndex = -1;
            rearIndex = -1;
        /* when front points to last index in the array
        need to change front back to 0.
         */
        } else if (frontIndex == length-1) {
            frontIndex = 0;
        } else{
            rearIndex--;
        }
        size--;
        return value;
    }
    public Type removeLast(){
        if (isEmpty()){
            return null;
        }
        Type value = items[rearIndex];
        if (frontIndex == rearIndex){
            frontIndex = -1;
            rearIndex = -1;
            /* if rear points at first element
            change it to point at length-1 spot
             */
        } else if (rearIndex == 0) {
            rearIndex = length -1;
        } else{
            rearIndex--;
        }
        size--;
        return value;
    }
    public Type get(int index){
        if(isEmpty()){
            return null;
        }
        return items[index];
    }
    public int size(){
        return size;
    }

    public boolean isEmpty(){
        return size == 0;
    }
    public boolean isFull(){
        return size == length;
    }
    public Type getLast(){
        return items[rearIndex];
    }
    public Type getFirst(){
        return items[frontIndex];
    }

    public static void main(String[] args){

        ArrayDeque<Integer> AD = new ArrayDeque();
        AD.addFirst(12);
        AD.addLast(33);
        AD.addFirst(14);
        int rmvF = AD.removeFirst();
        AD.addLast(11);
        AD.addFirst(54);
        int rmvL = AD.removeLast();
        int get1 = AD.get(0);
    }







}
