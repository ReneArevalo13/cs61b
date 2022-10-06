package deque;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {

    private static void printTimingTable(ArrayDeque<Integer> Ns, ArrayDeque<Double> times, ArrayDeque<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {

        //System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        ArrayDeque<String> ad1 = new ArrayDeque<>();

        assertTrue("A newly initialized LLDeque should be empty", ad1.isEmpty());
        ad1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, ad1.size());
        assertFalse("ad1 should now contain 1 item", ad1.isEmpty());

        ad1.addLast("middle");
        assertEquals(2, ad1.size());

        ad1.addLast("back");
        assertEquals(3, ad1.size());

        System.out.println("Printing out deque: ");
//		ad1.printDeque();

    }

    @Test
    /** Adds an item, then removes an item, and ensures that ad is empty afterwards. */
    public void addRemoveTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        // should be empty
        assertTrue("ad1 should be empty upon initialization", ad1.isEmpty());

        ad1.addFirst(10);
        // should not be empty
        assertFalse("ad1 should contain 1 item", ad1.isEmpty());

        ad1.removeFirst();
        // should be empty
        assertTrue("ad1 should be empty after removal", ad1.isEmpty());

    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        //System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addFirst(3);

        ad1.removeLast();
        ad1.removeFirst();
        ad1.removeLast();
        ad1.removeFirst();

        int size = ad1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);

    }

    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {


        ArrayDeque<String> lld1 = new ArrayDeque<String>();
        ArrayDeque<Double> lld2 = new ArrayDeque<Double>();
        ArrayDeque<Boolean> lld3 = new ArrayDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();

    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());


    }

    @Test
    public void randomizedTest() {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        ArrayDeque<Integer> B = new ArrayDeque<>();

        int N = 8;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int sizeA = L.size();
                int sizeB = B.size();

                assertEquals(sizeA, sizeB);
            } else if (operationNumber == 2) {
                // getLast
                if (L.size() == 0) {
                    continue;
                } else {
                    int lastA = L.getLast();
                    int lastB = B.getLast();
                    assertEquals(lastA, lastB);
                }
            } else if (operationNumber == 3) {
                // removeLast
                if (L.size() == 0) {
                    continue;
                } else {
                    L.removeLast();
                    B.removeLast();
                }

            }
        }
    }

    @Test
    public void resizeRandom() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        int N = 100000;
        for (int i = 0; i < N; i++) {
            int operationNumber = StdRandom.uniform(0, 2);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                ad1.addLast(randVal);
            } else if (operationNumber == 1) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                ad1.addFirst(randVal);
            }

        }
        assertEquals(ad1.size(), N);
    }

    @Test
    public void fillEmptyfill() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        for (int i = 0; i < 8; i++) {
            int randomNumber = StdRandom.uniform(0, 100);
            ad1.addLast(randomNumber);
        }
        for (int i = 0; i < 8; i++) {
            int output = ad1.removeFirst();
        }
        for (int i = 0; i < 8; i++) {
            int randomNumber = StdRandom.uniform(0, 100);
            ad1.addFirst(randomNumber);
        }
        assertEquals(ad1.size(), 8);
    }

    @Test
    public void testGet() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ArrayDeque<Integer> ad2 = new ArrayDeque<>();
        ad1.get(0);

        for (int i = 0; i < 8; i++) {
            int randomNumber = StdRandom.uniform(0, 100);
            ad1.addLast(randomNumber);
        }
        for (int i = 0; i < 8; i++) {
            int output = ad1.get(i);
            ad2.addFirst(output);
        }
    }

    @Test
    public void testGet2() {
        //Conceptual Deque: [f, c, a, b, d, e, g, h]
        ArrayDeque<String> ad1 = new ArrayDeque<>();
        ad1.addLast("a");
        ad1.addLast("b");
        ad1.addFirst("c");
        ad1.addLast("d");
        ad1.addLast("e");
        ad1.addFirst("f");
        ad1.addLast("g");
        ad1.addLast("h");

        String get0 = ad1.get(0);
        String get1 = ad1.get(1);
        String get2 = ad1.get(2);
        String get3 = ad1.get(3);
        String get4 = ad1.get(4);
        String get5 = ad1.get(5);
        String get6 = ad1.get(6);
        String get7 = ad1.get(7);


        String comp0 = "f";
        String comp1 = "c";
        String comp2 = "a";
        String comp3 = "b";
        String comp4 = "d";
        String comp5 = "e";
        String comp6 = "g";
        String comp7 = "h";

        assertEquals(comp0, get0);
        assertEquals(comp1, get1);
        assertEquals(comp2, get2);
        assertEquals(comp3, get3);
        assertEquals(comp4, get4);
        assertEquals(comp5, get5);
        assertEquals(comp6, get6);
        assertEquals(comp7, get7);


    }

    @Test
    public void testIndexing() {
        int[] indices = new int[8];
        for (int i = 0; i < 8; i++) {
            int front = 2;
            int rear = 3;
            int index = (i + rear) % indices.length;
            String indexString = Integer.toString(index);
            String counterString = Integer.toString(i);
            indices[i] = index;
            System.out.println("get " + counterString + " gets index " + indexString);

        }

    }
}
