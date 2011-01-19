package pacifism;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;

import deism.run.ExecutionGovernor;

import model.Board;
import model.Model;
import model.Segment;
import model.WallSegment;

/**
 * AWT component for game board
 */
public class GameBoardComponent extends JComponent {
    private static final long serialVersionUID = -7028947825699430811L;
    private final Model model;
    private final ExecutionGovernor governor;
    private final Tile[][] tiles;

    public GameBoardComponent(ExecutionGovernor governor, Model model) {
        super();
        this.model = model;
        this.governor = governor;

        // build the tile map
        Board board = model.getBoard();
        tiles = new Tile[board.getHeight()][];
        for (int row = 0; row < board.getHeight(); row++) {
            tiles[row] = new Tile[board.getWidth()];
            for (int col = 0; col < board.getWidth(); col++) {
                Segment seg = board.getSegment(col, row);
                boolean northOpen =
                        !(board.getSegment(col, row - 1) instanceof WallSegment);
                boolean eastOpen =
                        !(board.getSegment(col + 1, row) instanceof WallSegment);
                boolean southOpen =
                        !(board.getSegment(col, row + 1) instanceof WallSegment);
                boolean westOpen =
                        !(board.getSegment(col - 1, row) instanceof WallSegment);
                if (seg instanceof WallSegment) {
                    tiles[row][col] =
                            new WallTile(northOpen, eastOpen, southOpen,
                                    westOpen);
                }
                else {
                    tiles[row][col] = new NullTile();
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        final Board board = model.getBoard();
        Rectangle frameBounds = new Rectangle(getPreferredSize());

        g2d.setColor(Color.black);
        g2d.fill(frameBounds);
        g2d.draw(frameBounds);

        for (int row = 0; row < board.getHeight(); row++) {
            for (int col = 0; col < board.getWidth(); col++) {
                tiles[row][col].paint(col, row, g2d);
            }
        }

        long simtime = governor.getCurrentSimtime();
    }

    @Override
    public Dimension getPreferredSize() {
        final Board board = model.getBoard();
        return new Dimension(board.getWidth() * Tile.SIZE, board.getHeight()
                * Tile.SIZE);
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
