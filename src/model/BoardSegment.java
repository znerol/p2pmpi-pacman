package model;

/**
 * Abstract class for the board segments which get specialised in
 * {@link model.StreetSegment} and {@link model.WallSegment}.
 */
public abstract class BoardSegment implements Segment {
    private Board board;
    private int x;
    private int y;

    public BoardSegment(int x, int y, Board board) {
        this.board = board;
        this.x = x;
        this.y = y;
    }

    @Override
    public Segment getNorth() {
        return this.board.getSegment(x, y - 1);
    }

    @Override
    public Segment getEast() {
        return this.board.getSegment(x + 1, y);
    }

    @Override
    public Segment getSouth() {
        return this.board.getSegment(x, y + 1);
    }

    @Override
    public Segment getWest() {
        return this.board.getSegment(x - 1, y);
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public Board getBoard() {
        return this.board;
    }
}
