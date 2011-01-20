package model;

/**
 * A board segment which corresponds to one charachter in the board definition.
 * All Segments are doubled linked for easier queries
 */
public interface Segment {
    /**
     * North segment getter
     * 
     * @return the Segment in the north
     */
    public Segment getNorth();

    /**
     * East segment getter
     * 
     * @return the Segment in the east
     */
    public Segment getEast();

    /**
     * South segment getter
     * 
     * @return the Segment in the south
     */
    public Segment getSouth();

    /**
     * West segment getter
     * 
     * @return the Segment in the west
     */
    public Segment getWest();

    /**
     * The abscissa of the segment. It corresponds to the index in the board
     * definition
     * 
     * @return segment's abscissa
     */
    public int getX();

    /**
     * The ordinate of the segment. It corresponds to the index in the board
     * definition
     * 
     * @return segment's ordinate
     */
    public int getY();

    /**
     * is true if this segment is an instance of {@link model.StreetSegment}
     * 
     * @return true if it is a street segment
     */
    public boolean isStreet();

    /**
     * Returns the board instance
     * 
     * @return the board
     */
    public Board getBoard();
}
