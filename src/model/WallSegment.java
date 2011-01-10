package model;

public class WallSegment extends BoardSegment {

    public WallSegment(int x, int y, Board board) {
        super(x, y, board);
    }

    @Override
    public boolean isStreet() {
        return false;
    }
    
    @Override
    public String toString() {
        return "x";
    }

}
