package model;

public class StreetSegment extends Field {
    private StreetSegment north;
    private StreetSegment east;
    private StreetSegment south;
    private StreetSegment west;

    protected StreetSegment(int x, int y, Board board) {
        super(x, y, board);
    }

    public StreetSegment getNorthStreetSegment() {
        return north;
    }

    @Override
    protected void setNorth(Field field) {
        super.setNorth(field);
        if (field instanceof StreetSegment)
            this.north = (StreetSegment) field;
        else
            this.north = null;
    }

    public StreetSegment getEastStreetSegment() {
        return east;
    }

    @Override
    protected void setEast(Field field) {
        super.setEast(field);
        if (field instanceof StreetSegment)
            this.east = (StreetSegment) field;
        else
            this.east = null;
    }

    public StreetSegment getSouthStreetSegment() {
        return south;
    }

    @Override
    protected void setSouth(Field field) {
        super.setSouth(field);
        if (field instanceof StreetSegment)
            this.south = (StreetSegment) field;
        else
            this.south = null;
    }

    public StreetSegment getWestStreetSegment() {
        return west;
    }

    @Override
    protected void setWest(Field field) {
        super.setWest(field);
        if (field instanceof StreetSegment)
            this.west = (StreetSegment) field;
        else
            this.west = null;
    }
}
