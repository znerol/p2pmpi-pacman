package model;


public class Board {
    private Field[][] fields;
    private int width;
    private int height;
    
    public Board(char[][] boardDef) {
        height = boardDef.length;
        width = boardDef[0].length;
        
        fields = new Field[height][width];
        
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                switch(boardDef[h][w]) {
                case 'x':
                    fields[h][w] = new Wall(w, h, this);
                    break;
                case 'p':
                case 'g':
                case 's':
                case '.':
                    insertStreetSegment(w, h);
                    break;
                    default:
                        break;
                }
            }
        }
    }
    
    private void insertStreetSegment(int w, int h) {
        // Es wird angenommen, dass die Argumente immer in einem gültigen Bereich sind.
        // Der Aufbau vom Board beginnt oben links und geht nach rechts unten. Immer Row by Row. 
        // Die Karte muss immer einen Rand aus einem Wall Objekt haben.
        StreetSegment newSeg = new StreetSegment(w, h, this);
        fields[h][w] = newSeg;
        
        Street horizontal = fields[h-1][w].getHorizontalStreet();
        Street vertical = fields[h][w-1].getVerticalStreet();
        if (vertical != null) 
            vertical.addVerticalStreetSegment(newSeg);
        if (horizontal != null) 
            horizontal.addHorizontalStreetSegment(newSeg);
        
    }

    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public Field getField(int x, int y) {
        return fields[y][x];
    }
}
