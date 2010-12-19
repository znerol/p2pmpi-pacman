package model;

import paclib.GamePlay;

public abstract class Field {
    private int x;
    private int y;
    protected static final int FIELD_SIZE = GamePlay.GUI_FIELD_SIZE;
    private Board board;
    private Field north;
    private Field east;
    private Field south;
    private Field west;

    protected Field(int x, int y, Board board) {
        setBoard(board);
        setX(x);
        setY(y);

        setNeightbours();
    }

    protected void setNeightbours() {
        setNorth(this.board.getField(x, y - 1));
        getNorth().setSouth(this);
        setEast(this.board.getField(x + 1, y));
        getEast().setWest(this);
        setSouth(this.board.getField(x, y + 1));
        getSouth().setNorth(this);
        setWest(this.board.getField(x - 1, y));
        getWest().setEast(this);
    }

    public Board getBoard() {
        return board;
    }

    private void setBoard(Board board) {
        this.board = board;
    }

    public int getX() {
        return x;
    }

    private void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    private void setY(int y) {
        this.y = y;
    }

    public Field getNorth() {
        return north;
    }

    protected void setNorth(Field field) {
        north = field;
    }

    public Field getEast() {
        return east;
    }

    protected void setEast(Field field) {
        east = field;
    }

    public Field getSouth() {
        return south;
    }

    protected void setSouth(Field field) {
        south = field;
    }

    public Field getWest() {
        return west;
    }

    protected void setWest(Field field) {
        west = field;
    }
}
