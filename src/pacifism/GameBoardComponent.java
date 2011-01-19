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

/**
 * AWT component for game board
 */
public class GameBoardComponent extends JComponent {
    /**
     * Size of Pacman sprite
     */
    private static final int GUI_PAC_SIZE = 13;

    /**
     * Size of Ghost sprite
     */
    private static final int GUI_GHOST_SIZE = 13;

    /**
     * Size of one field
     */
    private static final int GUI_FIELD_SIZE = 18;

    private final Model model;
    private final ExecutionGovernor governor;

    public GameBoardComponent(ExecutionGovernor governor, Model model) {
        super();
        this.model = model;
        this.governor = governor;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        final Board board = model.getBoard();
        Rectangle frameBounds =
                new Rectangle(
                        board.getWidth() * GUI_FIELD_SIZE,
                        board.getHeight() * GUI_FIELD_SIZE);

        g2d.setColor(Color.black);
        g2d.fill(frameBounds);
        g2d.draw(frameBounds);

        long simtime = governor.getCurrentSimtime();
    }

    @Override
    public Dimension getPreferredSize() {
        final Board board = model.getBoard();
        return new Dimension(
                board.getWidth() * GUI_FIELD_SIZE,
                board.getHeight() * GUI_FIELD_SIZE);
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
