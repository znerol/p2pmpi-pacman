package model;

import java.util.HashMap;
import java.util.Map;

public class Board {
    private final Segment[][] segments;
    private final Map<Pair<Integer, Integer>, Waypoint> waypoints = new HashMap<Pair<Integer, Integer>, Waypoint>();
    private final int width;
    private final int height;

    public Board(char[][] boardDef) {
        height = boardDef.length;
        width = boardDef[0].length;
        
        segments = new Segment[height][width];
        
        populateSegements(boardDef);
    }
    
    protected void populateSegements(char[][] boardDef) {

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                switch (boardDef[h][w]) {
                case 'x':
                    segments[h][w] = new WallSegment(w, h, this);
                    break;
                default:
                    segments[h][w] = new StreetSegment(w, h, this);
                }
            }
        }
        
        populateWaypoints();
    }
    
    protected void populateWaypoints() {
        StreetSegment firstStreetSegment = null;
        for (Segment[] row : this.segments) {
            for (Segment cell : row) {
                if (cell instanceof StreetSegment)
                    firstStreetSegment = (StreetSegment)cell;
            }
        }
        
        if (firstStreetSegment == null) 
            throw new IllegalStateException();
        
        firstStreetSegment.populateWaypoints(waypoints);
    }
    
    public Waypoint getWaypoint(int x, int y) {
        return this.waypoints.get(new Pair<Integer, Integer>(x, y));
    }
    

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Segment getSegment(int x, int y) {
        return segments[(y + height) % height][(x + width) % width];
    }
    
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Segment[] row : this.segments) {
            for(Segment cell : row) {
                str.append(cell.toString());
            }
            str.append("\n");
        }
        return str.toString();
    }
}
