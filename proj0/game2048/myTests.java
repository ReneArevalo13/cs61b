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
                {2, 4, 2, 0},
                {2, 0, 2, 0},
                {0, 2, 2, 0},
                {4, 2, 2, 0},
        };

        b = new Board(before, 0);
        Model.checkColumn(b,0);
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
                {0, 2, 2, 0},
                {0, 0, 2, 0},
                {0, 32, 2, 0},
                {0, 32, 2, 0},
        };

        b = new Board(before, 0);
        Model.checkColumn(b,1);
        System.out.println(b);
    }
    @Test
    public void checkColumntest5(){
        int[][] before = new int[][] {
                {2, 0, 2, 0},
                {4, 4, 2, 2},
                {0, 4, 0, 0},
                {2, 4, 4, 8},
        };

        b = new Board(before, 0);
        Model.checkColumn(b,2);
        System.out.println(b);
    }
    @Test
    public void checkColumntest6(){
        int[][] before = new int[][] {
                {2, 0, 0, 0},
                {2, 0, 0, 0},
                {2, 0, 0, 0},
                {0, 0, 0, 0},
        };

        b = new Board(before, 0);
        System.out.println(b);

        b.setViewingPerspective(Side.SOUTH);
        System.out.println(b);

        Model.checkColumn(b,3);
        b.setViewingPerspective(Side.NORTH);
        System.out.println(b);
    }
    @Test
    public void checkColumntest7(){
        int[][] before = new int[][] {
                {2, 0, 0, 0},
                {2, 0, 0, 0},
                {2, 0, 0, 0},
                {0, 0, 0, 0},
        };

        b = new Board(before, 0);
        System.out.println(b);

        b.setViewingPerspective(Side.SOUTH);
        System.out.println(b);

        Model.checkColumn(b,3);
        b.setViewingPerspective(Side.NORTH);
        System.out.println(b);
    }
    @Test
    public void checkColumnTestFullLoop1(){
        int[][] before = new int[][] {
                {2, 0, 2, 2},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
        };

        b = new Board(before, 0);
        Board b2 = new Board(before,0);
        int score = 0;
        for (int i = 0; i < b.size(); i++){
            score += Model.checkColumn(b,i);

        }

        System.out.println(b2 == b);
        System.out.println(b);
        System.out.println(score);
    }
    @Test
    public void checkColumnTestFullLoop2(){
        int[][] before = new int[][] {
                {2, 0, 0, 0},
                {2, 0, 0, 0},
                {2, 0, 0, 0},
                {0, 0, 0, 0},
        };


        b = new Board(before, 0);
        int score = 0;
        System.out.println("South Executed");
        b.setViewingPerspective(Side.SOUTH);
        for (int i = 3; i >= 0; i--){
            score += Model.checkColumn(b,i);
        }
        b.setViewingPerspective(Side.NORTH);
        System.out.println(b);
        System.out.println(score);
    }
    @Test
    public void checkExtractRawValue1(){
        int[][] before = new int[][] {
                {2, 0, 2, 2},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
        };


        b = new Board(before, 0);
        int copyRaw[][];
        copyRaw = Model.extractRawValues(b);
        Board b2 = new Board(copyRaw, 0);
        System.out.println(b);
        System.out.println(b2);
        System.out.println(b == b2);
        String bString = b.toString();
        String b2String = b2.toString();
        System.out.println(b2String.equals(bString));

    }
    @Test
    public void checkChange1(){
        int[][] before = new int[][] {
                {2, 0, 0, 0},
                {2, 0, 0, 0},
                {2, 0, 0, 0},
                {0, 0, 0, 0},
        };


        b = new Board(before, 0);
        int score = 0;
        Board bCopy = Model.makeCopy(b);
        System.out.println(bCopy.toString());
        System.out.println("South Executed");
        b.setViewingPerspective(Side.SOUTH);
        for (int i = 3; i >= 0; i--){
            score += Model.checkColumn(b,i);
        }
        b.setViewingPerspective(Side.NORTH);
        System.out.println(b);
        System.out.println(Model.equalsAfterMove(b,bCopy));
        System.out.println(score);
    }
    @Test
    public void checkChange(){
        int[][] before = new int[][] {
                {2, 0, 2, 2},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
        };

        b = new Board(before, 0);
        Board bCopy = Model.makeCopy(b);
        Board b2 = new Board(before,0);
        int score = 0;
        for (int i = 0; i < b.size(); i++){
            score += Model.checkColumn(b,i);

        }

//        System.out.println(b2 == b);
        System.out.println(b);
        System.out.println(score);
        System.out.println(Model.equalsAfterMove(b,bCopy));
    }
}

