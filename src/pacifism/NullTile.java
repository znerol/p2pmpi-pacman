package pacifism;

import java.awt.Graphics2D;

public class NullTile implements Tile {
    @Override
    public void paint(int xindex, int yindex, Graphics2D g) {
        // draw nothing
    }
}
