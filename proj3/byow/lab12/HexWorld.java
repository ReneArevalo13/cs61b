package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;


import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private static final int WIDTH = 100;
    private static final int HEIGHT = 60;

    private static final long SEED = 2873120;
    private static final Random RANDOM = new Random(SEED);

    /**
     * Private class to abstract the location of an object in our world.
     */
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
        public Position shift (int dx, int dy) {
            return new Position(this.x + dx, this.y + dy);
        }
    }

    /**
     * Fills the given 2D array of tiles with RANDOM tiles.
     * @param tiles
     */
    public static void fillWithNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }
    public static void drawRow(TETile[][] tiles, Position p, TETile tile, int length) {
        for (int dx = 0; dx < length; dx++) {
            tiles[p.x + dx][p.y] = tile;
        }
    }
    /**
     * addHexagon helper using recursion
     */
    public static void addHexagonHelper(TETile[][] tiles, Position p, TETile tile, int b, int t) {
        //draw starting row
        Position startOfRow = p.shift(b, 0);
        drawRow(tiles, startOfRow, tile, t);
        //recursively draw remaining rows
        if (b > 0) {
            Position nextP = p.shift(0, -1);
            addHexagonHelper(tiles, nextP, tile, b - 1, t + 2);
        }
        //draw last row (same as first)
        Position startOfReflectedRow = startOfRow.shift(0, -(2*b + 1));
        drawRow(tiles, startOfReflectedRow, tile, t);

    }
    /***
     * method to draw a hexagon of size: SIZE positioned at point p.
     */
    public static void addHexagon(TETile[][] tiles, Position p, int size) {
        if (size < 2) return;
        addHexagonHelper(tiles, p, randomTile(), size - 1, size);

    }

    public static void addHexColumn(TETile[][] tiles, Position p, int size, int num) {
        if (num < 1) return;
        //draw this hexagon
        addHexagon(tiles, p, size);
        //draw n-1 hexagons below
        if (num > 1) {
            Position bottomNeighbor = getBottomNeighbor(p, size);
            addHexColumn(tiles, bottomNeighbor, size, num - 1);
        }
    }

    /**
     * gets bottom neighbor of tessellating hexagons
     * @param p
     * @return bottom neighbor where next hexagon will start
     */
    public static Position getBottomNeighbor(Position p, int n){
        return p.shift(0, -2*n);
    }
    public static Position getTopRightNeighbor(Position p, int n) {
        return p.shift(2*n - 1, n);
    }
    public static Position getBottomRightNeighbor(Position p, int n) {
        return p.shift(2*n - 1, -n);
    }

    public static void drawWorld(TETile[][] tiles, Position p,   int hexSize, int tessSize) {
        //draw first column
        addHexColumn(tiles, p, hexSize, tessSize);
        //expand up and to the right
        for (int i = 1; i < tessSize; i++) {
            p = getTopRightNeighbor(p, hexSize);
            addHexColumn(tiles, p, hexSize, tessSize + i);
        }
        //expand down and to the right
        for (int i = tessSize - 2; i >= 0; i--) {
            p = getBottomRightNeighbor(p, hexSize);
            addHexColumn(tiles, p, hexSize, tessSize + i );
        }
    }
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0: return Tileset.GRASS;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.SAND;
            case 3: return Tileset.MOUNTAIN;
            case 4: return Tileset.TREE;
            default: return Tileset.NOTHING;
        }
    }



    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        fillWithNothing(world);
        Position p = new Position(20, 54);
        drawWorld(world, p, 3, 3);

        ter.renderFrame(world);
    }


}
