package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private T[] items;
    /* integer of how many items in the array*/
    private int size;
    /* integer pointing to the element that is at the front of the array*/
    private int frontIndex;
    /* integer pointing to the element that is at the rear of the array*/
    private int rearIndex;
    /* length is how many spots are in the array */
    private int length;
    private Comparator<T> comparison;


    public MaxArrayDeque(Comparator<T> c) {
        super();
        comparison = c;
    }

    public T max() {
        if (isEmpty()) {
            return null;
        }
        T maximum = get(0);
        for (T i : items) {
            if (comparison.compare(i, maximum) > 0) {
                maximum = i;
            }
        }
        return maximum;
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T maximum = get(0);
        for (T i : items) {
            if (c.compare(i, maximum) > 0) {
                maximum = i;
            }
        }
        return maximum;
    }

//    private static class MaxComparator implements Comparator <MaxArrayDeque> {
//        public int compare(MaxArrayDeque a, MaxArrayDeque b){
//            return
//        }
//
//

//    public static void main(String[] args) {
//        MaxArrayDeque();
//    }


}
