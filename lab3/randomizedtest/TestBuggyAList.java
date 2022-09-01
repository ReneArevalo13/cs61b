package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.apache.commons.math3.optim.InitialGuess;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        BuggyAList<Integer> buggyA = new BuggyAList<Integer>();
        AListNoResizing<Integer> noResizingA = new AListNoResizing<Integer>();
        int firstAdd = 4;
        int secondAdd = 1;
        int thirdAdd = 9;
        buggyA.addLast(firstAdd);
        buggyA.addLast(secondAdd);
        buggyA.addLast(thirdAdd);
        noResizingA.addLast(firstAdd);
        noResizingA.addLast(secondAdd);
        noResizingA.addLast(thirdAdd);

        assertEquals(noResizingA.size(), buggyA.size());

        assertEquals(noResizingA.removeLast(), buggyA.removeLast());
        assertEquals(noResizingA.removeLast(), buggyA.removeLast());
        assertEquals(noResizingA.removeLast(), buggyA.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 5000;
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
}