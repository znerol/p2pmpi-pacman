package pacifism;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;

import deism.run.ExecutionGovernor;
import deism.util.Triple;

import model.Board;
import model.Direction;
import model.Model;
import model.Segment;
import model.StreetSegment;
import model.sprites.GhostState;
import model.sprites.PacmanState;
import model.sprites.Sprite;
import model.sprites.SpriteState;

/**
 * AWT component for game board
 */
public class GameBoardComponent extends JComponent {
    public static final int SCALE_FACTOR = 3;
    public static final int TILE_WAYPOINTS = 9;

    public static final int TILE_SIZE_PX = SCALE_FACTOR * TILE_WAYPOINTS;
    public static final int STREET_MARGIN_PX = SCALE_FACTOR;

    public static final int PAC_SIZE = 9;
    public static final int PAC_SIZE_PX = PAC_SIZE * SCALE_FACTOR;

    public static final int[] GHOST_ELLIPSE = { -4, -4, 9, 9 };
    public static final int[] GHOST_LEFT_EYE = { -2, -2, 1, 2 };
    public static final int[] GHOST_RIGHT_EYE = { 2, -2, 1, 2 };
    public static final int[] GHOST_RECT = { -4, 0, 9, 5 };
    public static final int[] GHOST_SKIRT_X = { -4, -2, 0, 2, 4, 6, 8 };
    public static final int[] GHOST_SKIRT_Y = { 5, 3, 5, 3, 5, 3, 5 };

    private static final long serialVersionUID = -7028947825699430811L;
    private final Model model;
    private final ExecutionGovernor governor;
    private final Area walls;

    /**
     * Animated ghost shapes.
     */
    private final Shape ghostShapes[];

    public GameBoardComponent(ExecutionGovernor governor, Model model) {
        super();
        this.model = model;
        this.governor = governor;

        // build the wall map by subtracting all the streets from the a
        // rectangular area
        Board board = model.getBoard();
        walls =
                new Area(new Rectangle(0, 0, board.getWidth() * TILE_SIZE_PX,
                        board.getHeight() * TILE_SIZE_PX));

        for (int row = 0; row < board.getHeight(); row++) {
            for (int col = 0; col < board.getWidth(); col++) {
                Segment seg = board.getSegment(col, row);
                if (seg instanceof StreetSegment) {
                    Rectangle rect =
                            new Rectangle(col * TILE_SIZE_PX, row
                                    * TILE_SIZE_PX, TILE_SIZE_PX, TILE_SIZE_PX);
                    rect.grow(STREET_MARGIN_PX, STREET_MARGIN_PX);
                    walls.subtract(new Area(rect));
                }
            }
        }

        // build ghost shapes
        Area base =
                new Area(new Ellipse2D.Float(GHOST_ELLIPSE[0],
                        GHOST_ELLIPSE[1], GHOST_ELLIPSE[2], GHOST_ELLIPSE[3]));
        base.add(new Area(new Rectangle(GHOST_RECT[0], GHOST_RECT[1],
                GHOST_RECT[2], GHOST_RECT[3])));
        base.subtract(new Area(new Ellipse2D.Float(GHOST_LEFT_EYE[0],
                GHOST_LEFT_EYE[1], GHOST_LEFT_EYE[2], GHOST_LEFT_EYE[3])));
        base.subtract(new Area(new Ellipse2D.Float(GHOST_RIGHT_EYE[0],
                GHOST_RIGHT_EYE[1], GHOST_RIGHT_EYE[2], GHOST_RIGHT_EYE[3])));

        Polygon skirt =
                new Polygon(GHOST_SKIRT_X, GHOST_SKIRT_Y, GHOST_SKIRT_X.length);

        AffineTransform scale =
                AffineTransform.getScaleInstance(SCALE_FACTOR, SCALE_FACTOR);

        ghostShapes = new Shape[4];
        for (int i = 0; i < ghostShapes.length; i++) {
            AffineTransform deplace =
                    AffineTransform.getTranslateInstance(-i, 0);
            Area ghost = new Area(base);
            ghost.subtract(new Area(deplace.createTransformedShape(skirt)));
            ghostShapes[i] = scale.createTransformedShape(ghost);
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
                g2d.translate(movement.b * SCALE_FACTOR, movement.c
                        * SCALE_FACTOR);
                g2d.setColor(Color.yellow);
                g2d.fill(pac);
                g2d.setTransform(new AffineTransform());
            }
            else if (state instanceof GhostState) {
                g2d.translate(movement.b * SCALE_FACTOR, movement.c
                        * SCALE_FACTOR);
                g2d.setColor(Color.red);
                g2d.fill(ghostShapes[((int) simtime >> 2) % ghostShapes.length]);
                g2d.setTransform(new AffineTransform());
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
        pac.setFrame(-PAC_SIZE_PX / 2, -PAC_SIZE_PX / 2, PAC_SIZE_PX,
                PAC_SIZE_PX);
        pac.setAngleStart(angle);
        pac.setAngleExtent(360 - 2 * angle);

        AffineTransform tx = new AffineTransform();

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
