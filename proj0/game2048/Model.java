package game2048;

import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author RENE AREVALO
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;


        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.
        Board b = this.board;
        if(side == Side.NORTH){
            for (int i = 0; i < b.size(); i++){
                this.score += Model.checkColumn(b,i);
            }
            b.setViewingPerspective(Side.NORTH);
            if(this.score < -4){
                changed = false;
            }else{
                changed = true;
            }

        }
        if(side == Side.EAST){
            b.setViewingPerspective(Side.EAST);
            for (int i = 3; i >= 0; i--){
                this.score += Model.checkColumn(b,i);
            }
            b.setViewingPerspective(Side.NORTH);
            changed = true;
        }
        if(side == Side.SOUTH){
            b.setViewingPerspective(Side.SOUTH);
            for (int i = 3; i >= 0; i--){
                this.score += Model.checkColumn(b,i);
            }
            b.setViewingPerspective(Side.NORTH);
            changed = true;
        }
        if(side == Side.WEST){
            b.setViewingPerspective(Side.WEST);
            for (int i = 3; i >= 0; i--){
                this.score += Model.checkColumn(b,i);
            }
            b.setViewingPerspective(Side.NORTH);
            changed = true;
        }


        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        // TODO: Fill in this function.
        for (int i = 0; i < b.size(); i++){
            for (int j = 0; j < b.size(); j++){
                if (b.tile(i, j) == null){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        // TODO: Fill in this function.
        for (int i = 0; i < b.size(); i++){
            for (int j = 0; j < b.size(); j++){
                if (b.tile(i,j) == null){
                } else if (b.tile(i,j).value() == MAX_PIECE) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        // TODO: Fill in this function.
        // check if at least one empty space exists on the board and that MAXPIECE isn't on the board
        if (emptySpaceExists(b)){
            return true;
        } else if (maxTileExists(b)) {
            return true;
        }
        // *2 check adjacent values
        for (int i = 0; i < b.size(); i++){
            for (int j = 0; j < b.size(); j++){

                if (compareAdjacent(b, i, j)){
                    return true;
                }
            }
        }
//
        return false;
        }

   public static boolean compareAdjacent(Board b, int i, int j){
        // compare adjacent tiles on the game board
       // would be cleaner if I made a function that did the comparison of tiles
        if (i == 0){
            if (j == 0) {
                return tileEqualAbove(b, i, j, "North") || tileEqualAbove(b, i, j, "East");
            }else if (j == 3) {
                return tileEqualAbove(b, i, j, "South") || tileEqualAbove(b, i, j, "East");
            }
            else{
                return tileEqualAbove(b, i, j, "South") || tileEqualAbove(b, i, j, "East")
                        || tileEqualAbove(b, i, j, "North");
            }
        } else if (i == 3) {
            if (j == 0){
                return tileEqualAbove(b, i, j, "West") || tileEqualAbove(b, i, j, "North");
            }
            else if (j == 3){
                return tileEqualAbove(b, i, j, "West") || tileEqualAbove(b, i, j, "South");
            }
            else{
                return tileEqualAbove(b, i, j, "South") || tileEqualAbove(b, i, j, "West")
                        || tileEqualAbove(b, i, j, "North");
            }
        } else if (j == 0){
            return tileEqualAbove(b, i, j, "West") || tileEqualAbove(b, i, j, "East")
                    || tileEqualAbove(b, i, j, "North");
        } else if (j == 3){
            return tileEqualAbove(b, i, j, "South")|| tileEqualAbove(b, i, j, "West")
                    || tileEqualAbove(b, i, j, "East");
        }else{
            return tileEqualAbove(b, i, j, "North") || tileEqualAbove(b, i, j, "East")
                    || tileEqualAbove(b, i, j, "South") || tileEqualAbove(b, i, j, "West");
        }


   }
   public static boolean tileEqualAbove(Board b, int i, int j, String direction){
        // helper function to check if tile in given direction is equal to tile at (i, j)
       return switch (direction) {
           case "North" -> b.tile(i, j).value() == b.tile(i, j + 1).value();
           case "East" -> b.tile(i, j).value() == b.tile(i + 1, j).value();
           case "South" -> b.tile(i, j).value() == b.tile(i, j - 1).value();
           case "West" -> b.tile(i, j).value() == b.tile(i - 1, j).value();
           default -> false;
       };
   }

   public static boolean emptyAbove(Board b, int i, int j) {
       // check to see if tiles above tile (i,j) are null
       if (j+1 > b.size() - 1){
//           System.out.println("j + 1 greater than size of board");
           return false;
       }
       return b.tile(i, j + 1) == null;
   }
   public static int howManyAbove(Board b, int i, int j){
        //recursively check how many empty tiles are above a given tile (i,j)
       if (j > b.size()){
           System.out.println("j has exceeded the bounds");
           return 0;
       }else if (emptyAbove(b, i, j)){
//           System.out.println("one iteration");
           return 1 + howManyAbove(b, i, j+1);
       }else{
           return 0;
           }
       }
//
    public static boolean executeMergeUp(Board b, int i, int j){
        if (j >= b.size()-1){
            return false;
        }
        if (tileEqualAbove(b, i, j,"North")){
            Tile t = b.tile(i,j);
            return b.move(i,j+1,t);
        }
        return false;
    }

    public static int checkColumn(Board b, int i){
        // method to check the moves of a given column, i
        boolean merged= false;
        int score = 0;
        int merges = 0;
        int moves = 0;

        for (int k = b.size()-2; k >= 0; k--){
            int j = k;
            Tile t = b.tile(i,j);

//            System.out.println("Current number of merges are " + merges);

//           System.out.println("j is " + j);
//            System.out.println(b);

            if (t == null){
                continue;

            }
            else if (emptyAbove(b, i, j)){
//               System.out.println("Move and merge clause");
                int move = howManyAbove(b, i, j);
                j = j+move;//reassign j so we follow the tile to the new position
                b.move(i,j,t);
                moves += 1;
//                System.out.println("TILE MOVED");
//                System.out.println(b);
//                System.out.println("pre merge " + merged);
                if(merges >= 1 && moves > 1){
                    merged = false;
                }
                if (!merged){
                    if(executeMergeUp(b,i,j)){
                        merged = true;
                        merges += 1;
//                       System.out.println("MERGE EXECUTED, post merge is " + merged);
//                       System.out.println(b.tile(i,j+1));
                        score += b.tile(i,j+1).value();
                    }
                }
            }else if(tileEqualAbove(b,i,j,"North")){
//               System.out.println("tile equal above clause");
//               System.out.println(merged);
                if(merges >= 1){
                    merged = true;
                }
                if (!merged){
                    if(executeMergeUp(b,i,j)){
                        merged = true;
                        merges += 1;
//                       System.out.println("MERGE EXECUTED, post merge is " + merged);
//                       System.out.println(b);
                        score += b.tile(i,j+1).value();
                    }
                }
            }else{
                continue;
            }
        }
//       System.out.println("Score is "+score);
        return score;

    }








    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
