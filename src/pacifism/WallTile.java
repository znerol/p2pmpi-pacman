package pacifism;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class WallTile implements Tile {
    private final Rectangle prototype;
    private final int OPEN_MARGIN = Tile.SIZE / 9;

    public WallTile(boolean northOpen, boolean eastOpen, boolean southOpen,
            boolean westOpen) {
        int x = westOpen ? OPEN_MARGIN : 0;
        int y = northOpen ? OPEN_MARGIN : 0;
        int w = Tile.SIZE - (westOpen ? OPEN_MARGIN : 0)
                        - (eastOpen ? OPEN_MARGIN : 0);
        int h = Tile.SIZE - (northOpen ? OPEN_MARGIN : 0)
                        - (southOpen ? OPEN_MARGIN : 0);
        prototype = new Rectangle(x, y, w, h);
    }

    @Override
    public void paint(int xindex, int yindex, Graphics2D g) {
        Rectangle frame = new Rectangle(prototype);
        frame.translate(xindex * Tile.SIZE, yindex * Tile.SIZE);

        g.setColor(Color.blue);
        g.fill(frame);
        // g.draw(rect);
    }
}
