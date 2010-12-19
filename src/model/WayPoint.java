package model;

import paclib.GamePlay;

public class WayPoint {
    private StreetSegment street;
    private int x;
    private int y;
    private WayPoint north;
    private WayPoint east;
    private WayPoint south;
    private WayPoint west;

    public static final int MIDDLE_X = GamePlay.GUI_FIELD_SIZE / 2;
    public static final int MIDDLE_Y = GamePlay.GUI_FIELD_SIZE / 2;

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
        this.street = street;
        setRelativeX(relativeX);
        setRelativeY(relativeY);
    }

    protected int getRelativeX() {
        return x;
    }

    protected void setRelativeX(int x) {
        this.x = x;
    }

    protected int getRelativeY() {
        return y;
    }

    protected void setRelativeY(int y) {
        this.y = y;
    }

    protected int getAbsolutX() {
        return x + getField().getX();
    }

    protected int getAbsolutY() {
        return y + getField().getY();
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
