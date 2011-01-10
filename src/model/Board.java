package model;

public class Board {
    private Segment[][] segments;
    private int width;
    private int height;

    public Board(char[][] boardDef) {
        populateSegements(boardDef);
    }
    
    protected void populateSegements(char[][] boardDef) {
        height = boardDef.length;
        width = boardDef[0].length;
        
        segments = new Segment[height][width];

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
        
        firstStreetSegment.populateWaypoints();
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
