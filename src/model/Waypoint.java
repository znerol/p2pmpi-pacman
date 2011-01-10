package model;

import paclib.GamePlay;
import model.sprites.Sprite;

public class Waypoint {
    private Waypoint north;
    private Waypoint east;
    private Waypoint south;
    private Waypoint west;
    private int x;
    private int y;
    private int absoluteX;
    private int absoluteY;
    private Sprite sprite;
    private StreetSegment owner;
    
    public Waypoint(StreetSegment owner, int x, int y) {
        this.x = x;
        this.y = y;
        this.absoluteX = owner.getX() * GamePlay.GUI_FIELD_SIZE + x;
        this.absoluteY = owner.getY() * GamePlay.GUI_FIELD_SIZE + y;
        this.owner = owner;
    }
    
    public boolean isDirectionAvailable(Direction dir) {
        return getNextWaypoint(dir) != null;
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
            return this;
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

    public Sprite getSprite() {
        return this.sprite;
    }
    
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public StreetSegment getOwner() {
        return owner;
    }

}
