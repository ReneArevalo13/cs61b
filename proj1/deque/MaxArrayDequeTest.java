package deque;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;
import org.junit.Test;

import java.util.Comparator;


import static deque.MaxArrayDeque.getMaxIntComparator;
import static deque.MaxArrayDeque.getMaxStringComparator;
import static org.junit.Assert.*;

public class MaxArrayDequeTest {

    @Test
    public void integerMaxTest1() {
        Comparator<Integer> mc = getMaxIntComparator();
        MaxArrayDeque<Integer> mad = new MaxArrayDeque(mc);
        mad.addFirst(2);
        mad.addLast(33);
        mad.addFirst(1);
        mad.addLast(44);
        mad.addFirst(53);
        mad.addLast(42);
        mad.addFirst(98);
        mad.addFirst(1);
        int maxNumber = mad.max();
        int maxKnown = 98;
        assertEquals(maxNumber, maxKnown);
//        System.out.println(maxNumber);
    }

    @Test
    public void integerMaxTest2() {
        Comparator<Integer> mc = getMaxIntComparator();
        MaxArrayDeque<Integer> mad = new MaxArrayDeque(mc);
        mad.addFirst(2);
        mad.addLast(33);
        mad.addFirst(1);
        mad.addLast(44);
        mad.addFirst(53);
        mad.addLast(42);
        mad.addFirst(98);
        mad.addFirst(1);
        mad.addFirst(56);
        mad.addLast(990);
        mad.addLast(232);
        int maxNumber = mad.max();
        int maxKnown = 990;
        assertEquals(maxNumber, maxKnown);
//        System.out.println(maxNumber);
    }

    @Test
    public void integerMaxTest3() {
        Comparator<Integer> mc = getMaxIntComparator();
        MaxArrayDeque<Integer> mad = new MaxArrayDeque(mc);
        mad.addFirst(2);
        mad.addLast(33);
        mad.addFirst(1);
        mad.addLast(44);
        mad.addFirst(53);
        mad.addLast(42);
        mad.addFirst(98);
        mad.addFirst(1);
        int maxNumber = mad.max(getMaxIntComparator());
        int maxKnown = 98;
        assertEquals(maxNumber, maxKnown);
//        System.out.println(maxNumber);
    }

    @Test
    public void stringMaxTest1() {
        Comparator <String> mc = getMaxStringComparator();
        MaxArrayDeque<String> mad = new MaxArrayDeque(mc);
        mad.addLast("happy");
        mad.addFirst("apple");
        mad.addLast("power");
        mad.addFirst("blessed");
        mad.addLast("smart");
        mad.addLast("inexplicable");
        String maxWord = "inexplicable";

        assertEquals(maxWord, mad.max());

    }
    @Test
    public void stringMaxTest2() {
        Comparator <String> mc = getMaxStringComparator();
        MaxArrayDeque<String> mad = new MaxArrayDeque(mc);
        mad.addLast("happy");
        mad.addFirst("apple");
        mad.addLast("power");
        mad.addFirst("blessed");
        mad.addLast("smart");
        mad.addLast("inexplicable");
        mad.addFirst("juicy");
        mad.addFirst("prolific");
        mad.addFirst("jhdfajsfksdffsdfe");
        String maxWord = "jhdfajsfksdffsdfe";

        assertEquals(maxWord, mad.max());

    }


}
