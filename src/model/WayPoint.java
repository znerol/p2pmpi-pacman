package model;

import java.awt.Point;

import paclib.GamePlay;

public class WayPoint {
    private StreetSegment street;
    private final Point relativePoint = new Point(MIDDLE_X, MIDDLE_Y);
    private WayPoint north;
    private WayPoint east;
    private WayPoint south;
    private WayPoint west;

    public static final int MIDDLE_X = GamePlay.GUI_FIELD_SIZE / 2;
    public static final int MIDDLE_Y = GamePlay.GUI_FIELD_SIZE / 2;
    public static final int FIELD_SIZE = GamePlay.GUI_FIELD_SIZE;

    public static void populateWayPoints(StreetSegment street) {
        new WayPoint(street, MIDDLE_X, MIDDLE_Y);
    }

    /**
     * Constructor
     * 
     * @param field
     *            WayPoint's field
     */
    private WayPoint(StreetSegment street, int relativeX, int relativeY) {
        assert (street != null);

        setStreetSegment(street, relativeX, relativeY);

        setRelativeX(relativeX);
        setRelativeY(relativeY);

        connectWayPoints();

        if (north == null)
            setNextWayPoint(relativeX - 1, relativeY);
        if (west == null)
            setNextWayPoint(relativeX, relativeY - 1);
        if (south == null)
            setNextWayPoint(relativeX + 1, relativeY);
        if (east == null)
            setNextWayPoint(relativeX, relativeY + 1);
    }

    private void connectWayPoints() {
        int relativeX = getRelativeX();
        int relativeY = getRelativeY();

        if (relativeX == MIDDLE_X) {
            this.south = getWayPoint(MIDDLE_X, relativeY + 1);
            this.north = getWayPoint(MIDDLE_X, relativeY - 1);
        } else {
            this.south = getWayPoint(MIDDLE_X, relativeY + 1);
            this.north = getWayPoint(MIDDLE_X, relativeY - 1);
        }

        if (relativeY == MIDDLE_Y) {
            this.west = getWayPoint(relativeX - 1, MIDDLE_Y);
            this.east = getWayPoint(relativeX + 1, MIDDLE_Y);
        } else {
            this.west = getWayPoint(relativeX, MIDDLE_Y);
            this.east = getWayPoint(relativeX, MIDDLE_Y);
        }
    }

    private WayPoint getWayPoint(int relativeX, int relativeY) {
        return null;
    }

    // TODO
    private void setNextWayPoint(int relativeX, int relativeY) {
        if (relativeX < MIDDLE_X && street.getEastStreetSegment() == null)
            return;
        if (relativeX > MIDDLE_X && street.getWestStreetSegment() == null)
            return;
        if (relativeY < MIDDLE_Y && street.getNorthStreetSegment() == null)
            return;
        if (relativeY > MIDDLE_Y && street.getSouthStreetSegment() == null)
            return;

        if (street.getNorthStreetSegment() != null) {

        }
        if (street.getEastStreetSegment() != null) {

        }
        if (street.getSouthStreetSegment() != null) {

        }
        if (street.getWestStreetSegment() != null) {

        }
    }

    /**
     * Set Streets Segment. If points are out of bounds, the next tile is
     * assigned.
     * 
     * @param street
     *            Current street
     * @param relativeX
     *            Relative abscissa
     * @param relativeY
     *            Relative ordinate
     */
    private void setStreetSegment(StreetSegment street, int relativeX,
            int relativeY) {
        if (relativeX >= FIELD_SIZE && relativeY >= 0 && relativeY < FIELD_SIZE)
            this.street = street.getEastStreetSegment();
        else if (relativeX < 0 && relativeY >= 0 && relativeY < FIELD_SIZE)
            this.street = street.getWestStreetSegment();
        else if (relativeY >= FIELD_SIZE && relativeX >= 0
                && relativeX < FIELD_SIZE)
            this.street = street.getSouthStreetSegment();
        else if (relativeY < 0 && relativeX >= 0 && relativeX < FIELD_SIZE)
            this.street = street.getNorthStreetSegment();
        else if (relativeX >= 0 && relativeX < FIELD_SIZE && relativeY >= 0
                && relativeY < FIELD_SIZE)
            this.street = street;

        if (this.street == null)
            throw new RuntimeException("Invalid Coordinates");
        // Segment is diagonal which is not valid
        throw new RuntimeException("Diagnol segment");
    }

    /**
     * Returns relative abscissa
     * 
     * @return Relative abscissa
     */
    public int getRelativeX() {
        return relativePoint.x;
    }

    /**
     * Sets relative abscissa. Corrects out of bounds coordinates
     * 
     * @param x
     *            Relative abscissa
     */
    protected void setRelativeX(int x) {
        relativePoint.x = (x + FIELD_SIZE) % FIELD_SIZE;
    }

    /**
     * Relative abscissa within field
     * 
     * @return Relative abscissa within field
     */
    public int getRelativeY() {
        return relativePoint.y;
    }

    /**
     * Relative ordinate within field. Corrects out of bounds coordinates
     * 
     * @return Relative ordinate within field
     */
    protected void setRelativeY(int y) {
        relativePoint.y = (y + FIELD_SIZE) % FIELD_SIZE;
    }

    /**
     * Absolute abscissa
     * 
     * @return Absolute abscissa
     */
    public int getAbsoluteX() {
        return relativePoint.x + getField().getX();
    }

    /**
     * Absolute ordinate
     * 
     * @return Absolute ordinate
     */
    public int getAbsoluteY() {
        return relativePoint.y + getField().getY();
    }

    /**
     * Absolute point within game
     * 
     * @return Absolute point within game
     */
    public Point getAbsolutePoint() {
        return new Point(getAbsoluteX(), getAbsoluteY());
    }

    /**
     * Returns relative point within field
     * 
     * @return Relative point within field
     */
    public Point getRelativePoint() {
        return new Point(relativePoint);
    }

    /**
     * Returns the next valid WayPoint for a spite.
     * 
     * @param sprite
     *            Current Spite
     * @return Next WayPoint
     */
    public WayPoint getNextWayPoint(Sprite sprite) {
        WayPoint next = getNextWayPoint(sprite.getNextDirection());
        if (next == null)
            return getNextWayPoint(sprite.getDirection());
        return next;
    }

    /**
     * Returns the next valid WayPoint in given direction
     * 
     * @param dir
     *            Spite direction.
     * @return Next WayPoint
     */
    protected WayPoint getNextWayPoint(Direction dir) {
        switch (dir) {
        case North:
            return north;
        case East:
            return east;
        case South:
            return south;
        case West:
            return west;
        default:
            return null;
        }
    }

    /**
     * @return the field
     */
    public StreetSegment getField() {
        return street;
    }
}
