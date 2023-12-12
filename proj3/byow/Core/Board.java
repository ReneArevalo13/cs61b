package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.lab12.HexWorld;

import java.util.Random;

/**
 * This class will handle the rendering of the board.
 */
public class Board {
    private static class Position {
        int x;
        int y;
        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * method to shift the given position object over an amount in the x and y directions
         * by dx and dy respectively.
         * @param dx how much to shift in the x direction
         * @param dy how much to shift in the y direction
         * @return a new position object that has been moved dx in the x dir and dy in y dir
         */
        public Board.Position shift (int dx, int dy) {
            return new Board.Position(this.x + dx, this.y + dy);
        }
    }

    private static final int WIDTH = 40;
    private static final int HEIGHT = 40;
    private static final long SEED = 873129;
    private static final Random RANDOM = new Random(SEED);
    public static void emptyBoard(TETile[][] tiles) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
        ter.renderFrame(tiles);
    }
    public static void drawHorizontalWall(TETile[][] tiles, TETile tile, int length, Position p)  {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        for (int i = 0; i < length; i++) {
            tiles[p.x + i][p.y] = tile;
        }
        ter.renderFrame(tiles);
    }
    public static void drawVerticalWall(TETile[][] tiles, TETile tile, int length, Position p)  {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        for (int j = 0; j < length; j++) {
            tiles[p.x][p.y + j] = tile;
        }
        ter.renderFrame(tiles);
    }
    public static void drawRectangle(TETile[][] tiles, TETile tile, int height,
                                     int width, Position p)  {
        drawVerticalWall(tiles, tile, height, p);
        drawHorizontalWall(tiles, tile, width, p);
        Position startOfUpperRow = p.shift(0, height - 1);
        Position startOfRightMostRow = p.shift(width - 1, 0);
        drawVerticalWall(tiles, tile, height, startOfRightMostRow);
        drawHorizontalWall(tiles, tile, width, startOfUpperRow);
    }
    public static void randomRectangle(TETile[][] tiles, TETile tile, Position p) {
        //need to omit the possibility of a 0 length; thus we add 1 to the random number
        int height = RANDOM.nextInt(20 - 1) + 1;
        int width = RANDOM.nextInt(20 - 1) + 1;
        drawRectangle(tiles, tile, height, width, p);
    }
    public static void buildRoom(TETile[][] tiles, int height,
                                 int width, Position p) {
        //construct the walls of the room
        drawRectangle(tiles, Tileset.WALL, height, width, p);
        //fill the room with floor tiles
          // adjust the start position of the "floor"
        Position startOfFloor = p.shift(1, 1);
        filledRectangle(tiles, Tileset.TREE, height - 2 , width - 2 , startOfFloor);

    }
    public static void buildRandomRoom(TETile[][] tiles, Position p) {
        int height = RANDOM.nextInt(20 - 1) + 1;
        int width = RANDOM.nextInt(20 - 1) + 1;
        buildRoom(tiles, height, width, p);

    }
    public static void filledRectangle(TETile[][] tiles, TETile tile, int height,
                                       int width, Position p) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tiles[p.x + i][p.y + j] = tile;
            }
        }
        ter.renderFrame(tiles);

    }


    public static void main(String[] args) {
        TETile[][] tester = new TETile[WIDTH][HEIGHT];
        Position p = new Position(10, 15);
        emptyBoard(tester);
//        filledRectangle(tester, Tileset.MOUNTAIN,10, 10, p);
//        drawHorizontalWall(tester, Tileset.WALL, 10, p);
//        drawVerticalWall(tester, Tileset.WALL, 10, p);
//        drawRectangle(tester, Tileset.WALL, 6, 12, p);
//        randomRectangle(tester, Tileset.WALL, p);
//        buildRoom(tester, 10 ,10 , p);
        buildRandomRoom(tester, p);
    }


}
