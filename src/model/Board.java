package model;

import java.util.HashMap;
import java.util.Map;

import deism.util.Pair;

/**
 * A static board for the game. The board is divided into
 * {@link model.StreetSegment} and {@link model.WallSegment}. Further, the
 * {@link model.StreetSegment} contains the waypoints where the sprites acts on.
 * 
 * Within a game, only one instance of this board can be created.
 */
public class Board {
    private final Segment[][] segments;
    private final Map<Pair<Integer, Integer>, Waypoint> waypoints = new HashMap<Pair<Integer, Integer>, Waypoint>();
    private final int width;
    private final int height;
    private static Board board;

    public Board(char[][] boardDef) {
        assert (Board.board == null);

        Board.board = this;

        height = boardDef.length;
        width = boardDef[0].length;

        segments = new Segment[height][width];

        populateSegements(boardDef);
    }

    /**
     * Returns the static board instance.
     * 
     * @return the static board instance.
     */
    public static Board getBoard() {
        return Board.board;
    }

    /**
     * Scans over the board definition and creates for each tile a corresponding
     * {@link model.Segment}.
     * 
     * @param boardDef
     *            a board definition
     */
    protected void populateSegements(char[][] boardDef) {

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                switch (boardDef[h][w]) {
                // A wall segement can never be occupied by a sprite.
                case 'x':
                    segments[h][w] = new WallSegment(w, h, this);
                    break;
                // Everything else has to be a street segment
                default:
                    segments[h][w] = new StreetSegment(w, h, this);
                }
            }
        }

        populateWaypoints();
    }

    /**
     * Scans through every {@link model.StreetSegment} instance and populates
     * all the waypoints on it.
     */
    protected void populateWaypoints() {
        StreetSegment firstStreetSegment = null;
        for (Segment[] row : this.segments) {
            for (Segment cell : row) {
                if (cell instanceof StreetSegment)
                    firstStreetSegment = (StreetSegment) cell;
            }
        }

        if (firstStreetSegment == null)
            throw new IllegalStateException();

        firstStreetSegment.populateWaypoints(waypoints);
    }

    /**
     * Returns the {@link model.Waypoint} on the given coordinates.
     * 
     * @param x
     *            abscissa
     * @param y
     *            ordinate
     * @return a {@link model.Waypoint} at the given coordinates or null.
     */
    public Waypoint getWaypoint(int x, int y) {
        return this.waypoints.get(new Pair<Integer, Integer>(x, y));
    }

    /**
     * The board width
     * 
     * @return board with
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * The board height
     * 
     * @return board height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * The {@link model.Segment} at the given point. Invalid points will get
     * wrapped around.
     * 
     * @param x
     *            abscissa
     * @param y
     *            ordinate
     * @return the Segment instance at the given point.
     */
    public Segment getSegment(int x, int y) {
        return segments[(y + height) % height][(x + width) % width];
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Segment[] row : this.segments) {
            for (Segment cell : row) {
                str.append(cell.toString());
            }
            str.append("\n");
        }
        return str.toString();
    }
}
