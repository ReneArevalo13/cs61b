package deque;

import java.util.Comparator;


public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparison;

    private T[] maxItems;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        maxItems = getItems();
        comparison = c;
    }

    public T max() {
        if (isEmpty()) {
            return null;
        }
        T maximum = get(0);
        for (T i : maxItems) {
            if (i == null) {
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
        for (T i : maxItems) {
            if (c.compare(i, maximum) > 0) {
                maximum = i;
            }
        }
        return maximum;
    }

    private static class MaxIntComparator implements Comparator<Integer> {
        public int compare(Integer a, Integer b) {
            return a - b;
        }
    }
    private static class MaxStringComparator implements Comparator<String> {
        public int compare(String  a, String  b) {
            return a.length() - b.length();
        }
    }
//    public static Comparator<Integer> getMaxIntComparator() {
//        return new MaxIntComparator();
//    }
//    public static Comparator<String> getMaxStringComparator() {
//        return new MaxStringComparator();
//    }


//
//

//    public static void main(String[] args) {
//        Comparator<Integer> mc = getMaxIntComparator();
//        MaxArrayDeque<Integer> mad = new MaxArrayDeque(mc);
//
//        mad.addFirst(2);
//        mad.addLast(33);
//        mad.addFirst(1);
//        mad.addLast(44);
//        mad.addFirst(53);
//        int maxNumber = mad.max();
//        System.out.println(maxNumber);
//
//    }


}
