package model;

import java.util.ArrayList;
import java.util.List;

import deism.util.Pair;

import paclib.GamePlay;

/**
 * Represents a waypoint in the model. Every waypoint is double connected to its
 * neighbours and once to its owner segment. The sprites in the game can walk on
 * this waypoints an perform discrete jumps between them.
 * 
 * Every waypoint has an absolute coordination on the board an a relative
 * coordination to its owner segment.
 * 
 * A waypoint can be either a normal waypoint, a junction or a change view
 * waypoint if the waypoint is on the border of a junction segment.
 */
public class Waypoint {
    private Waypoint north;
    private Waypoint east;
    private Waypoint south;
    private Waypoint west;
    private final int x;
    private final int y;
    private final int absoluteX;
    private final int absoluteY;
    private final StreetSegment owner;
    private final boolean isJunction;
    private List<Direction> directions = null;

    public Waypoint(StreetSegment owner, int x, int y) {
        this.x = x;
        this.y = y;
        this.absoluteX = owner.getX() * GamePlay.GUI_FIELD_SIZE + x;
        this.absoluteY = owner.getY() * GamePlay.GUI_FIELD_SIZE + y;
        this.owner = owner;

        if (owner.isJunction() && owner.getWaypointCentre() == null)// == this)
            this.isJunction = true;
        else
            this.isJunction = false;
    }

    /**
     * Determins the direction to the owners segment. Returns Direction.None if
     * the waypoint is the centre.
     * 
     * @return Direction to centre
     */
    public Direction getDirectionToCentre() {
        Waypoint centre = owner.getWaypointCentre();

        if (centre.getRelativeX() > this.getRelativeX())
            return Direction.East;
        else if (centre.getRelativeX() < this.getRelativeX())
            return Direction.West;
        else if (centre.getRelativeY() > this.getRelativeY())
            return Direction.South;
        else if (centre.getRelativeY() < this.getRelativeY())
            return Direction.North;
        else
            return Direction.None;
    }

    /**
     * Determines the next waypoint that has a special property like
     * "isJunction" or "isChangingView".
     * 
     * @param dir
     *            Direction to investigate
     * @return Next interesting waypoint
     */
    public Waypoint getNextPointOfInterest(Direction dir) {
        Waypoint next = null;
        switch (dir) {
        case North:
            next = north;
            break;
        case East:
            next = east;
            break;
        case South:
            next = south;
            break;
        case West:
            next = west;
            break;
        default:
            return null;
        }

        if (next == null)
            return null;
        if (next.isJunction())
            return next;
        if (next.isChangingView(dir))
            return next;

        // If nothing interesting so far, look further recursively.
        return next.getNextPointOfInterest(dir);
    }

    /**
     * Determines all possible directions on this waypoint
     * 
     * @return All possible directions away from this waypoint.
     */
    public List<Direction> getPossibleDirections() {
        if (this.directions != null)
            return this.directions;

        this.directions = new ArrayList<Direction>();
        if (north != null)
            this.directions.add(Direction.North);
        if (east != null)
            this.directions.add(Direction.East);
        if (south != null)
            this.directions.add(Direction.South);
        if (west != null)
            this.directions.add(Direction.West);
        return this.directions;

    }

    /**
     * Returns true if is junction point
     * 
     * @return true if junction
     */
    public boolean isJunction() {
        return this.isJunction;
    }

    /**
     * Returns true if the view changes on this waypoint in given direction
     * 
     * @param currentDir
     *            Current direction
     * @return true if view changes
     */
    public boolean isChangingView(Direction currentDir) {
        switch (currentDir) {
        case North:
            return south.owner != owner
                    && (south.owner.isJunction() || owner.isJunction());
        case East:
            return west.owner != owner
                    && (west.owner.isJunction() || owner.isJunction());
        case South:
            return north.owner != owner
                    && (north.owner.isJunction() || owner.isJunction());
        case West:
            return west.owner != owner
                    && (west.owner.isJunction() || owner.isJunction());
        default:
            return false;
        }
    }

    /**
     * Determines that a waypoint is available on given direction
     * 
     * @param dir
     *            current direction
     * @return true if next waypoint not equls null
     */
    public boolean isDirectionAvailable(Direction dir) {
        return getNextWaypoint(dir) != null;
    }

    /**
     * Determines if two waypoints are connected in a straight line
     * 
     * @param other
     *            Opposite waypoint
     * @return true if horizontal or vertical connected.
     */
    public boolean directConnected(Waypoint other) {
        return getDistance(other) != null;
    }

    /**
     * Determines the distance between two waypoints and the direction.
     * 
     * Works only if two waypoints are directly connected with each other {@see
     * directConnected(Waypoint other)}
     * 
     * @param other
     *            Opposite waypoint
     * @param distance
     *            distance so far
     * @param dir
     *            Direction of investigation
     * @return A pair of a direction and distance
     */
    protected Pair<Direction, Integer> getDistance(Waypoint other,
            int distance, Direction dir) {
        if (other == null)
            return null;
        if (other == this)
            return new Pair<Direction, Integer>(dir, distance);

        switch (dir) {
        case North:
            return getDistance(other.south, distance + 1, dir);
        case East:
            return getDistance(other.west, distance + 1, dir);
        case South:
            return getDistance(other.north, distance + 1, dir);
        case West:
            return getDistance(other.east, distance + 1, dir);
        default:
            return null;
        }
    }

    /**
     * Determines the euclidean distance of two waypoints. They does not have to
     * be connected
     * 
     * @param other
     *            Opposite waypoint
     * @return positive distance between two waypoints
     */
    public double getEuclideanDistance(Waypoint other) {
        int dx = other.getAbsoluteX() - getAbsoluteX();
        int dy = other.getAbsoluteY() - getAbsoluteY();

        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Determines the distance between two direct connected waypoints
     * 
     * @param other
     *            Opposite waypoint
     * @return a pair of the direction and distance to the opposite waypoint.
     */
    public Pair<Direction, Integer> getDistance(Waypoint other) {

        Pair<Direction, Integer> result = null;
        if (other.absoluteX == this.absoluteX) {
            result = getDistance(other, 0, Direction.North);
            result = result != null ? result : getDistance(other, 0,
                    Direction.South);
        } else if (other.absoluteY == this.absoluteY) {
            result = getDistance(other, 0, Direction.East);
            result = result != null ? result : getDistance(other, 0,
                    Direction.West);
        }
        return result;
    }

    /**
     * Determines the distance to the next wall segment in the given direction
     * 
     * @param dir
     *            DIrection under investigation
     * @return Distance in waypoints to the next wall segment
     */
    public Integer getDistanceToWall(Direction dir) {
        Waypoint next = getNextWaypoint(dir);
        if (next == null)
            return 0;
        return next.getDistanceToWall(dir) + 1;
    }

    /**
     * Gets the next Waypoint in the given direction
     * 
     * @param dir
     *            Direction
     * @return Waypoint in direction
     */
    public Waypoint getNextWaypoint(Direction dir) {
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
     * Abscissa relative to owner segment
     * 
     * @return abscissa to owner segment
     */
    public int getRelativeX() {
        return this.x;
    }

    /**
     * Ordinate relative to owner segment
     * 
     * @return ordinate to owner segment
     */
    public int getRelativeY() {
        return this.y;
    }

    /**
     * Absolute abscissa of this waypoint on the board.
     * 
     * @return abscissa of waypoint
     */
    public int getAbsoluteX() {
        return this.absoluteX;
    }

    /**
     * Absolute ordinate of this waypoint on the board.
     * 
     * @return ordinate of waypoint
     */

    public int getAbsoluteY() {
        return this.absoluteY;
    }

    /**
     * Getter north waypoint
     * 
     * @return north waypoint
     */
    public Waypoint getNorth() {
        return north;
    }

    /**
     * Setter north waypoint
     * 
     * @param north
     *            waypoint in the north
     */
    public void setNorth(Waypoint north) {
        this.north = north;
        if (north != null)
            north.south = this;
    }

    /**
     * Getter east waypoint
     * 
     * @return east waypoint
     */
    public Waypoint getEast() {
        return east;
    }

    /**
     * Setter east waypoint
     * 
     * @param east
     *            waypoint in the east
     */
    public void setEast(Waypoint east) {
        this.east = east;
        if (east != null)
            east.west = this;
    }

    /**
     * Getter south waypoint
     * 
     * @return south waypoint
     */
    public Waypoint getSouth() {
        return south;
    }

    /**
     * Setter south waypoint
     * 
     * @param south
     *            waypoint in the south
     */
    public void setSouth(Waypoint south) {
        this.south = south;
        if (south != null)
            south.north = this;
    }

    /**
     * Getter west waypoint
     * 
     * @return west waypoint
     */
    public Waypoint getWest() {
        return west;
    }

    /**
     * Setter west waypoint
     * 
     * @param west
     *            waypoint in the west
     */
    public void setWest(Waypoint west) {
        this.west = west;
        if (west != null)
            west.east = this;
    }

    /**
     * Getter owner segment
     * 
     * @return owner
     */
    public StreetSegment getOwner() {
        return owner;
    }
}
