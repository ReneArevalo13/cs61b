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

        public int size(){
        return size;
    }

    public boolean isEmpty(){
        return size == 0;
    }
    public boolean isFull(){
        return size == length;
    }

    public static void main(String[] args){

        ArrayDeque<Integer> AD = new ArrayDeque();
        AD.addFirst(12);
        AD.addLast(33);
        AD.addFirst(14);
    }







}
