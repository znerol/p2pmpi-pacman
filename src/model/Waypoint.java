package model;

import java.util.ArrayList;
import java.util.List;

import paclib.GamePlay;

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
        
        if (owner.isJunction() && owner.getWaypointCentre() == this)
            this.isJunction = true;
        else
            this.isJunction = false;
    }
    
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
    
    public boolean isJunction() {
        return this.isJunction;
    }
    
    public boolean isChangingView(Direction currentDir) {
        switch(currentDir) {
        case North:
            return south.owner != owner && (south.owner.isJunction() || owner.isJunction());
        case East:
            return west.owner != owner && (west.owner.isJunction() || owner.isJunction());
        case South:
            return north.owner != owner && (north.owner.isJunction() || owner.isJunction());
        case West:
            return west.owner != owner && (west.owner.isJunction() || owner.isJunction());
        default:
            return false;
        }
    }
    
    public boolean isDirectionAvailable(Direction dir) {
        return getNextWaypoint(dir) != null;
    }
    
    public boolean directConnected(Waypoint other) {
        return getDistance(other) != null;
    }
    
    protected Pair<Direction, Integer> getDistance(Waypoint other, int distance, Direction dir) {
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
    
    public Pair<Direction, Integer> getDistance(Waypoint other) {

        Pair<Direction, Integer> result = null;
        if (other.absoluteX == this.absoluteX) {
            result = getDistance(other, 0, Direction.North);
            result = result != null ? result : getDistance(other, 0, Direction.South);
        } else if (other.absoluteY == this.absoluteY) {
            result = getDistance(other, 0, Direction.East);
            result = result != null ? result : getDistance(other, 0, Direction.West);
        } 
        return result;
    }
    
    public Integer getDistanceToWall(Direction dir) {
        Waypoint next = getNextWaypoint(dir);
        if (next == null) 
            return 0;
        return next.getDistanceToWall(dir) + 1;
    }
    
    public Waypoint getNextWaypoint(Direction dir) {
        switch(dir) {
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
    
    public int getRelativeX() {
        return this.x;
    }
    
    public int getRelativeY() {
        return this.y;
    }
    
    public int getAbsoluteX() {
        return this.absoluteX;
    }
    
    public int getAbsoluteY() {
        return this.absoluteY;
    }
    
    public Waypoint getNorth() {
        return north;
    }

    public void setNorth(Waypoint north) {
        this.north = north;
        if (north != null)
            north.south = this;
    }

    public Waypoint getEast() {
        return east;
    }

    public void setEast(Waypoint east) {
        this.east = east;
        if (east != null)
            east.west = this;
    }

    public Waypoint getSouth() {
        return south;
    }

    public void setSouth(Waypoint south) {
        this.south = south;
        if (south != null)
            south.north = this;
    }

    public Waypoint getWest() {
        return west;
    }

    public void setWest(Waypoint west) {
        this.west = west;
        if (west != null)
            west.east = this;
    }

    public StreetSegment getOwner() {
        return owner;
    }
}
