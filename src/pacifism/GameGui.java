package pacifism;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import deism.run.ExecutionGovernor;

import model.Model;

/**
 *
 */
public class GameGui extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = 6359668977567197864L;

    private final JLabel labelPoints;
    private final JLabel labelLives;
    private final Timer refreshTimer;

    /**
     * Standart Konstruktor
     * 
     */
    public GameGui(ExecutionGovernor governor, KeyListener keyboardController,
            Model model) {
        super("P2PMPI Pacman");

        GameBoardComponent boardComponent =
                new GameBoardComponent(governor, model);

        setLayout(new BorderLayout());
        add(boardComponent, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2));
        labelPoints = new JLabel("Points: 0");
        labelLives = new JLabel("Lives: 3");
        panel.add(labelPoints);
        panel.add(labelLives);
        add(panel, BorderLayout.SOUTH);

        pack();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.addKeyListener(keyboardController);

        refreshTimer = new Timer(1000/60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        refreshTimer.start();
    }

    public void printPoints(int points) {
        labelPoints.setText("Points: " + points);
        labelPoints.repaint();
    }

    public void printLives(int lives) {
        labelLives.setText("Lives: " + lives);
        labelLives.repaint();
    }
}
