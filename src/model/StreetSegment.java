package model;

import java.util.Map;

import deism.util.Pair;

import paclib.GamePlay;

/**
 * A StreetSegment in the board definition where the sprites can act on. A
 * StreetSegments contains many {@link model.Waypoint}s and knows exactly, which
 * waypoint is in the centre and are on the border to othter StreetSegments.
 */
public class StreetSegment extends BoardSegment {
    private static int HALF_SIZE = GamePlay.GUI_FIELD_SIZE / 2;

    private Waypoint centre;
    private Waypoint north;
    private Waypoint east;
    private Waypoint south;
    private Waypoint west;

    public StreetSegment(int x, int y, Board board) {
        super(x, y, board);
    }

    /**
     * Is true if this segment is a junction respectivally a curve. A
     * StreetSegment is a junction as well if it is a dead end since the sprites
     * have to turn there.
     * 
     * @return true if junction
     */
    public boolean isJunction() {
        boolean result = ((getSouth().isStreet() || getNorth().isStreet()) && (getEast()
                .isStreet() || getWest().isStreet())) || isDeadEnd();
        if (result)
            return true;
        else
            return false;
    }

    /**
     * Is true if only one other StreetSegment is at its border
     * 
     * @return true if dead end
     */
    public boolean isDeadEnd() {
        int count = getSouth().isStreet() ? 1 : 0;
        count += getWest().isStreet() ? 1 : 0;
        count += getEast().isStreet() ? 1 : 0;
        count += getNorth().isStreet() ? 1 : 0;
        return count == 1;
    }

    /**
     * Checks an other StreetSegment if they are on the same street, i.e., are
     * horizontally or vertically connected
     * 
     * @param other
     *            Opposite StreetSegment
     * @return true if direct connected
     */
    public boolean directConnected(StreetSegment other) {
        if (other == this)
            return true;
        else if (other.getX() == this.getX())
            return verticalDirectConnected(this, other)
                    || verticalDirectConnected(other, this);
        else if (other.getY() == this.getY())
            return horizontalDirectConnected(this, other)
                    || horizontalDirectConnected(other, this);
        else
            return false;
    }

    /**
     * Checks if two StreetSegments are vertically connected
     * 
     * @param hight
     *            StreetSegement which is above of low
     * @param low
     *            StreetSegement which is under of high
     * @return true if vertically connected
     */
    protected boolean verticalDirectConnected(StreetSegment high,
            StreetSegment low) {
        do {
            if (low.getNorth() instanceof StreetSegment)
                low = (StreetSegment) low.getNorth();
            else
                return false;
        } while (low != null && low != high);
        return low != null;
    }

    /**
     * Checks if two StreetSegments are horizontally connected
     * 
     * @param hight
     *            StreetSegement which is right of left
     * @param low
     *            StreetSegement which is left of right
     * @return true if horizontally connected
     */
    protected boolean horizontalDirectConnected(StreetSegment left,
            StreetSegment right) {
        do {
            if (left.getEast() instanceof StreetSegment)
                left = (StreetSegment) left.getEast();
            else
                return false;
        } while (left != null && left != right);
        return left != null;
    }

    @Override
    public boolean isStreet() {
        return true;
    }

    @Override
    public String toString() {
        return ".";
    }

    /**
     * return the waypoint in the north
     * 
     * @return waypoint north or null
     */
    public Waypoint getWaypointNorth() {
        return north;
    }

    /**
     * return the waypoint in the east
     * 
     * @return waypoint east or null
     */
    public Waypoint getWaypointEast() {
        return east;
    }

    /**
     * return the waypoint in the south
     * 
     * @return waypoint south or null
     */
    public Waypoint getWaypointSouth() {
        return south;
    }

    /**
     * return the waypoint in the west
     * 
     * @return waypoint west or null
     */
    public Waypoint getWaypointWest() {
        return west;
    }

    /**
     * return the waypoint in the centre
     * 
     * @return waypoint centre or null
     */
    public Waypoint getWaypointCentre() {
        return centre;
    }

    /**
     * Populates all {@link model.Waypoint} instances in this segment.
     * 
     * @param waypoints
     */
    public void populateWaypoints(
            Map<Pair<Integer, Integer>, Waypoint> waypoints) {
        if (centre != null)
            return;

        this.centre = new Waypoint(this, HALF_SIZE, HALF_SIZE);
        waypoints.put(
                new Pair<Integer, Integer>(centre.getAbsoluteX(), centre
                        .getAbsoluteY()), centre);
        populateNorth(waypoints);
        populateEast(waypoints);
        populateSouth(waypoints);
        populateWest(waypoints);
    }

    /**
     * Populates all {@link model.Waypoint}s of the centre's north
     * 
     * @param waypoints
     *            Waypoint register
     */
    private void populateNorth(Map<Pair<Integer, Integer>, Waypoint> waypoints) {
        if (!getNorth().isStreet())
            return;

        north = centre;
        for (int i = 0; i < HALF_SIZE; i++) {
            Waypoint newWaypoint = new Waypoint(this, north.getRelativeX(),
                    north.getRelativeY() - 1);
            waypoints.put(new Pair<Integer, Integer>(
                    newWaypoint.getAbsoluteX(), newWaypoint.getAbsoluteY()),
                    newWaypoint);
            north.setNorth(newWaypoint);
            north = north.getNorth();
        }

        StreetSegment street = (StreetSegment) getNorth();
        north.setNorth(street.getWaypointSouth());
        street.populateWaypoints(waypoints);
    }

    /**
     * Populates all {@link model.Waypoint}s of the centre's east
     * 
     * @param waypoints
     *            Waypoint register
     */
    private void populateEast(Map<Pair<Integer, Integer>, Waypoint> waypoints) {
        if (!getEast().isStreet())
            return;

        east = centre;
        for (int i = 0; i < HALF_SIZE; i++) {
            Waypoint newWaypoint = new Waypoint(this, east.getRelativeX() + 1,
                    east.getRelativeY());
            waypoints.put(new Pair<Integer, Integer>(
                    newWaypoint.getAbsoluteX(), newWaypoint.getAbsoluteY()),
                    newWaypoint);
            east.setEast(newWaypoint);
            east = east.getEast();
        }

        StreetSegment street = (StreetSegment) getEast();
        east.setEast(street.getWaypointWest());
        street.populateWaypoints(waypoints);
    }

    /**
     * Populates all {@link model.Waypoint}s of the centre's south
     * 
     * @param waypoints
     *            Waypoint register
     */
    private void populateSouth(Map<Pair<Integer, Integer>, Waypoint> waypoints) {
        if (!getSouth().isStreet())
            return;

        south = centre;
        for (int i = 0; i < HALF_SIZE; i++) {
            Waypoint newWaypoint = new Waypoint(this, south.getRelativeX(),
                    south.getRelativeY() + 1);
            waypoints.put(new Pair<Integer, Integer>(
                    newWaypoint.getAbsoluteX(), newWaypoint.getAbsoluteY()),
                    newWaypoint);
            south.setSouth(newWaypoint);
            south = south.getSouth();
        }

        StreetSegment street = (StreetSegment) getSouth();
        south.setSouth(street.getWaypointNorth());
        street.populateWaypoints(waypoints);
    }

    /**
     * Populates all {@link model.Waypoint}s of the centre's west
     * 
     * @param waypoints
     *            Waypoint register
     */
    private void populateWest(Map<Pair<Integer, Integer>, Waypoint> waypoints) {
        if (!getWest().isStreet())
            return;

        west = centre;
        for (int i = 0; i < HALF_SIZE; i++) {
            Waypoint newWaypoint = new Waypoint(this, west.getRelativeX() - 1,
                    west.getRelativeY());
            waypoints.put(new Pair<Integer, Integer>(
                    newWaypoint.getAbsoluteX(), newWaypoint.getAbsoluteY()),
                    newWaypoint);
            west.setWest(newWaypoint);
            west = west.getWest();
        }

        StreetSegment street = (StreetSegment) getWest();
        west.setWest(street.getWaypointEast());
        street.populateWaypoints(waypoints);
    }
}
