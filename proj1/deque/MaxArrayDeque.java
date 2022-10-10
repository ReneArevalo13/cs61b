package deque;

import edu.princeton.cs.algs4.In;

import java.util.Comparator;


public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparison;
    ArrayDeque<T> maxItems;


    public MaxArrayDeque(Comparator<T> c) {
        super();
//        maxItems = new ArrayDeque<T>();
        comparison = c;
    }

    public T max() {
        if (isEmpty()) {
            return null;
        }
        T maximum = get(0);
        for (T i : items) {
            if (i == null){
                continue;
            }
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

    private static class MaxComparator implements Comparator <Integer> {
        public int compare(Integer a, Integer b) {
            return a - b;
        }
    }
    public static Comparator<Integer> getMaxComparator() {
        return new MaxComparator();
    }


//
//

    public static void main(String[] args) {
        Comparator<Integer> mc = getMaxComparator();
        MaxArrayDeque<Integer> mad = new MaxArrayDeque(mc);

        mad.addFirst(2);
        mad.addLast(33);
        mad.addFirst(1);
        mad.addLast(44);
        int maxNumber = mad.max();
        System.out.println(maxNumber);

    }


}
