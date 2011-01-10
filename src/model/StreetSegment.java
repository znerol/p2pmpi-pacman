package model;

import paclib.GamePlay;

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

    @Override
    public boolean isStreet() {
        return true;
    }
    
    @Override
    public String toString() {
        return ".";
    }
    
    public Waypoint getWaypointNorth() {
        return north;
    }
    
    public Waypoint getWaypointEast() {
        return east;
    }
    
    public Waypoint getWaypointSouth() {
        return south;
    }
    
    public Waypoint getWaypointWest() {
        return west;
    }
    
    public Waypoint getWaypointCentre() {
        return centre;
    }
    
    public void populateWaypoints() {
        if (centre != null)
            return;
        
        this.centre = new Waypoint(this, HALF_SIZE, HALF_SIZE);
        populateNorth();
        populateEast();
        populateSouth();
        populateWest();
    }
    
    private void populateNorth() {
        if (!getNorth().isStreet())
            return;
        
        north = centre;
        for(int i = 0; i < HALF_SIZE; i++) {
            north.setNorth(new Waypoint(this, north.getRelativeX(), north.getRelativeY() - 1));
            north = north.getNorth();
        }
        
        StreetSegment street = (StreetSegment)getNorth();
        north.setNorth(street.getWaypointSouth());
        street.populateWaypoints();
    }
    
    private void populateEast() {
        if (!getEast().isStreet())
            return;
        
        east = centre;
        for(int i = 0; i < HALF_SIZE; i++) {
            east.setEast(new Waypoint(this, east.getRelativeX() + 1, east.getRelativeY()));
            east = east.getEast();
        }
        
        StreetSegment street = (StreetSegment)getEast();
        east.setEast(street.getWaypointWest());
        street.populateWaypoints();
    }
    
    private void populateSouth() {
        if (!getSouth().isStreet())
            return;
        
        south = centre;
        for(int i = 0; i < HALF_SIZE; i++) {
            south.setSouth(new Waypoint(this, south.getRelativeX(), south.getRelativeY() + 1));
            south = south.getSouth();
        }

        StreetSegment street = (StreetSegment)getSouth();
        south.setSouth(street.getWaypointNorth());
        street.populateWaypoints();
    }
    
    private void populateWest() {
        if (!getWest().isStreet())
            return;
        
        west = centre;
        for(int i = 0; i < HALF_SIZE; i++) {
            west.setWest(new Waypoint(this, west.getRelativeX() - 1, west.getRelativeY()));
            west = west.getWest();
        }
        
        StreetSegment street = (StreetSegment)getWest();
        west.setWest(street.getWaypointEast());
        street.populateWaypoints();
    }
}
