package pacifism;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;

import javax.swing.JComponent;

import deism.run.ExecutionGovernor;

import model.Board;
import model.Model;
import model.Segment;
import model.StreetSegment;

/**
 * AWT component for game board
 */
public class GameBoardComponent extends JComponent {
    public static int TILE_SIZE = 27;
    public static int STREET_MARGIN = 3;

    private static final long serialVersionUID = -7028947825699430811L;
    private final Model model;
    private final ExecutionGovernor governor;
    private final Area walls;

    public GameBoardComponent(ExecutionGovernor governor, Model model) {
        super();
        this.model = model;
        this.governor = governor;

        // build the wall map by subtracting all the streets from the a
        // rectangular area
        Board board = model.getBoard();
        walls = new Area(new Rectangle(0, 0, board.getWidth() * TILE_SIZE,
                        board.getHeight() * TILE_SIZE));

        for (int row = 0; row < board.getHeight(); row++) {
            for (int col = 0; col < board.getWidth(); col++) {
                Segment seg = board.getSegment(col, row);
                if (seg instanceof StreetSegment) {
                    Rectangle rect =
                            new Rectangle(col * TILE_SIZE, row * TILE_SIZE,
                                    TILE_SIZE, TILE_SIZE);
                    rect.grow(STREET_MARGIN, STREET_MARGIN);
                    walls.subtract(new Area(rect));
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // background
        g2d.setColor(Color.black);
        g2d.fill(walls.getBounds());

        // walls
        g2d.setColor(Color.blue);
        g2d.fill(walls);

        long simtime = governor.getCurrentSimtime();
    }

    @Override
    public Dimension getPreferredSize() {
        return walls.getBounds().getSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
}
