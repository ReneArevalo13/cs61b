package deque;

import java.util.Comparator;


public class MaxArrayDeque<T> extends ArrayDeque<T> {
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
        for (T i : this) {
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
        for (T i : this) {
            if (i == null) {
                continue;
            }
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



}
