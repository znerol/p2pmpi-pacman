package pacifism;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;

import javax.swing.JComponent;

import deism.run.ExecutionGovernor;

import model.Board;
import model.Direction;
import model.Model;
import model.Segment;
import model.StreetSegment;
import model.Triple;
import model.sprites.GhostState;
import model.sprites.PacmanState;
import model.sprites.Sprite;
import model.sprites.SpriteState;

/**
 * AWT component for game board
 */
public class GameBoardComponent extends JComponent {
    public static int TILE_SIZE = 27;
    public static int STREET_MARGIN = 3;
    public static int PAC_SIZE = 23;
    public static int GHOST_SIZE = 23;

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
        walls =
                new Area(new Rectangle(0, 0, board.getWidth() * TILE_SIZE,
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

        // FIXME: draw sprites
        for (Sprite sprite : model.getSprites()) {
            SpriteState state = sprite.getCurrentState();
            Triple<Direction, Integer, Integer> movement =
                state.nextPosition(simtime);

            if (state instanceof PacmanState) {
                Shape pac = pacShape(simtime, movement.a);
                g2d.translate(movement.b, movement.c);
                g2d.setColor(Color.yellow);
                g2d.fill(pac);
            }
            else if (state instanceof GhostState) {
                Rectangle ghost = new Rectangle(GHOST_SIZE, GHOST_SIZE);
                g2d.translate(movement.b, movement.c);                
                g2d.setColor(Color.red);
                g2d.fill(ghost);
            }
        }
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

    private Shape pacShape(long simtime, Direction direction) {
        // 0 30 60 30 (angle)
        // -1 0 1 0 (alter)
        // => bit zero: value, bit one signfactor

        Arc2D pac = new Arc2D.Float();

        int signfact = 1 - (((int) simtime >> 2) & 0x2);
        int alter = signfact * (((int) simtime >> 2) & 0x1);
        int angle = 30 + 30 * alter;

        pac.setArcType(Arc2D.PIE);
        pac.setFrame(-PAC_SIZE / 2, -PAC_SIZE / 2, PAC_SIZE, PAC_SIZE);
        pac.setAngleStart(angle);
        pac.setAngleExtent(360 - 2 * angle);

        AffineTransform tx = new AffineTransform();

        // Set origin to 0, 0
        // FIXME: perhaps we can remove this one
        tx.translate(TILE_SIZE / 2, TILE_SIZE / 2);

        // rotate according to direction
        int quadrant = 0;
        switch (direction) {
        case North:
            quadrant++;
        case West:
            quadrant++;
        case South:
            quadrant++;
        }
        tx.quadrantRotate(quadrant);

        return tx.createTransformedShape(pac);
    }
}
