package model;

import java.util.Map;

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
    
    public boolean isJunction() {
        return ((getSouth().isStreet() || getNorth().isStreet()) && (getEast().isStreet() || getWest().isStreet())) || isDeadEnd(); 
    }
    
    public boolean isDeadEnd() {
        int count = getSouth().isStreet() ? 1 : 0;
        count += getWest().isStreet() ? 1 : 0;
        count += getEast().isStreet() ? 1 : 0;
        count += getNorth().isStreet() ? 1 : 0;
        return count == 1;
    }
    
    public boolean directConnected(StreetSegment other) {
        if (other == this)
            return true;
        else if (other.getX() == this.getX())
            return verticalDirectConnected(this, other) || verticalDirectConnected(other, this);
        else if (other.getY() == this.getY())
                return horizontalDirectConnected(this, other) || horizontalDirectConnected(other, this);
        else
            return false;
    }
    
    protected boolean verticalDirectConnected(StreetSegment hight, StreetSegment low) {
        do {
            if (low.getNorth() instanceof StreetSegment)
                low = (StreetSegment)low.getNorth();
            else 
                return false;
        } while (low != null && low != hight);
        return low != null;
    }
    
    protected boolean horizontalDirectConnected(StreetSegment left, StreetSegment right) {
        do {
            if (left.getEast() instanceof StreetSegment)
                left = (StreetSegment)left.getEast();
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
    
    public void populateWaypoints(Map<Pair<Integer, Integer>, Waypoint> waypoints) {
        if (centre != null)
            return;
        
        this.centre = new Waypoint(this, HALF_SIZE, HALF_SIZE);
        waypoints.put(new Pair<Integer, Integer>(centre.getAbsoluteX(), centre.getAbsoluteY()), centre);
        populateNorth(waypoints);
        populateEast(waypoints);
        populateSouth(waypoints);
        populateWest(waypoints);
    }
    
    private void populateNorth(Map<Pair<Integer, Integer>, Waypoint> waypoints) {
        if (!getNorth().isStreet())
            return;
        
        north = centre;
        for(int i = 0; i < HALF_SIZE; i++) {
            Waypoint newWaypoint = new Waypoint(this, north.getRelativeX(), north.getRelativeY() - 1);
            waypoints.put(new Pair<Integer, Integer>(newWaypoint.getAbsoluteX(), newWaypoint.getAbsoluteY()), newWaypoint);
            north.setNorth(newWaypoint);
            north = north.getNorth();
        }
        
        StreetSegment street = (StreetSegment)getNorth();
        north.setNorth(street.getWaypointSouth());
        street.populateWaypoints(waypoints);
    }
    
    private void populateEast(Map<Pair<Integer, Integer>, Waypoint> waypoints) {
        if (!getEast().isStreet())
            return;
        
        east = centre;
        for(int i = 0; i < HALF_SIZE; i++) {
            Waypoint newWaypoint = new Waypoint(this, east.getRelativeX() + 1, east.getRelativeY());
            waypoints.put(new Pair<Integer, Integer>(newWaypoint.getAbsoluteX(), newWaypoint.getAbsoluteY()), newWaypoint);
            east.setEast(newWaypoint);
            east = east.getEast();
        }
        
        StreetSegment street = (StreetSegment)getEast();
        east.setEast(street.getWaypointWest());
        street.populateWaypoints(waypoints);
    }
    
    private void populateSouth(Map<Pair<Integer, Integer>, Waypoint> waypoints) {
        if (!getSouth().isStreet())
            return;
        
        south = centre;
        for(int i = 0; i < HALF_SIZE; i++) {
            Waypoint newWaypoint = new Waypoint(this, south.getRelativeX(), south.getRelativeY() + 1);
            waypoints.put(new Pair<Integer, Integer>(newWaypoint.getAbsoluteX(), newWaypoint.getAbsoluteY()), newWaypoint);
            south.setSouth(newWaypoint);
            south = south.getSouth();
        }

        StreetSegment street = (StreetSegment)getSouth();
        south.setSouth(street.getWaypointNorth());
        street.populateWaypoints(waypoints);
    }
    
    private void populateWest(Map<Pair<Integer, Integer>, Waypoint> waypoints) {
        if (!getWest().isStreet())
            return;
        
        west = centre;
        for(int i = 0; i < HALF_SIZE; i++) {
            Waypoint newWaypoint = new Waypoint(this, west.getRelativeX() - 1, west.getRelativeY());
            waypoints.put(new Pair<Integer, Integer>(newWaypoint.getAbsoluteX(), newWaypoint.getAbsoluteY()), newWaypoint);
            west.setWest(newWaypoint);
            west = west.getWest();
        }
        
        StreetSegment street = (StreetSegment)getWest();
        west.setWest(street.getWaypointEast());
        street.populateWaypoints(waypoints);
    }
}
