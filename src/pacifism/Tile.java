package pacifism;

import java.awt.Graphics2D;

public interface Tile {
    public final int SIZE = 27;
    public void paint(int xindex, int yindex, Graphics2D g);
}
