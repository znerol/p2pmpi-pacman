package model;


public interface Segment {
    public Segment getNorth();
    public Segment getEast();
    public Segment getSouth();
    public Segment getWest();
    
    public int getX();
    public int getY();
    
    public boolean isStreet();
    
    public Board getBoard();
}
