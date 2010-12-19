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
                switch (boardDef[h][w]) {
                case 'x':
                    fields[h][w] = new Wall(w, h, this);
                    break;
                case 'p':
                case 'g':
                case 's':
                case '.':
                    fields[h][w] = new StreetSegment(w, h, this);
                    break;
                default:
                    break;
                }
            }
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Field getField(int x, int y) {
        return fields[y % height][x % width];
    }
}
