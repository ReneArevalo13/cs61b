package game2048;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class myTests {
    static Board b;
    public static void main(String[] args){
        int[][] rawVals = new int[][] {
                {0, 0, 4, 0},
                {0, 0, 0, 0},
                {0, 2, 0, 0},
                {0, 0, 0, 0},
        };
        b = new Board(rawVals, 0);
//        System.out.println(b.size());
        System.out.println(Model.howManyAbove(b,0,0));
//        Model.howManyAbove(b,1,1);

    }

    @Test
    public void testHowManyAbove(){
        int[][] rawVals = new int[][] {
                {0, 0, 4, 0},
                {0, 0, 0, 0},
                {0, 2, 0, 0},
                {0, 0, 0, 0},
        };
        b = new Board(rawVals, 0);
        Model.howManyAbove(b,0,0);
    }
    @Test
    public void testEmptyAbove1(){
        int[][] rawVals = new int[][] {
                {0, 0, 4, 0},
                {0, 0, 0, 0},
                {0, 2, 0, 0},
                {0, 0, 0, 0},
        };
        b = new Board(rawVals, 0);
        assertTrue("tile above is empty "
                        + "(there is empty space on the board)\n" + b,
                Model.emptyAbove(b, 0, 2));
    }
    @Test
    public void testEmptyAbove2(){
        int[][] rawVals = new int[][] {
                {2, 4, 2, 2},
                {4, 2, 4, 2},
                {2, 4, 2, 4},
                {4, 2, 4, 2},
        };
        b = new Board(rawVals, 0);
        assertFalse("tile above is not empty "
                        + "(there is empty space on the board)\n" + b,
                Model.emptyAbove(b, 2, 0));
    }
    @Test
    public void testExecuteMergeUp1(){
        int[][] before = new int[][] {
                {0, 0, 0, 0},
                {0, 0, 2, 0},
                {0, 0, 2, 0},
                {0, 0, 0, 0},
        };

        b = new Board(before, 0);
        Model.executeMergeUp(b,2,1);
        System.out.println(b);
    }
    @Test
    public void testExecuteMergeUp2(){
        int[][] before = new int[][] {
                {0, 0, 0, 0},
                {0, 0, 2, 0},
                {0, 4, 2, 0},
                {0, 4, 0, 0},
        };

        b = new Board(before, 0);
        Model.executeMergeUp(b,1,0);
        System.out.println(b);
    }
    @Test
    public void testExecuteMergeUp3(){
        int[][] before = new int[][] {
                {0, 0, 0, 0},
                {0, 0, 2, 0},
                {0, 4, 2, 0},
                {0, 4, 0, 0},
        };

        b = new Board(before, 0);
        Model.executeMergeUp(b,1,3);
        System.out.println(b);
    }

    @Test
    public void checkColumntest1(){
        int[][] before = new int[][] {
                {0, 0, 0, 0},
                {0, 0, 2, 0},
                {0, 4, 2, 0},
                {0, 4, 0, 0},
        };

        b = new Board(before, 0);
        Model.checkColumn(b,1);
        System.out.println(b);
    }
    @Test
    public void checkColumntest2(){
        int[][] before = new int[][] {
                {0, 0, 0, 0},
                {0, 0, 2, 0},
                {0, 4, 2, 0},
                {0, 4, 0, 0},
        };

        b = new Board(before, 0);
        Model.checkColumn(b,2);
        System.out.println(b);
    }
    @Test
    public void checkColumntest3(){
        int[][] before = new int[][] {
                {0, 2, 0, 0},
                {0, 0, 2, 0},
                {0, 4, 2, 0},
                {0, 4, 0, 0},
        };

        b = new Board(before, 0);
        Model.checkColumn(b,1);
        System.out.println(b);
    }
    @Test
    public void checkColumntest4(){
        int[][] before = new int[][] {
                {0, 2, 0, 0},
                {0, 0, 2, 0},
                {0, 32, 2, 0},
                {0, 32, 0, 0},
        };

        b = new Board(before, 0);
        Model.checkColumn(b,1);
        System.out.println(b);
    }
    @Test
    public void checkColumntest5(){
        int[][] before = new int[][] {
                {2, 2, 2, 0},
                {4, 4, 2, 2},
                {0, 4, 0, 0},
                {2, 4, 2, 8},
        };

        b = new Board(before, 0);
        Model.checkColumn(b,1);
        System.out.println(b);
    }
}
