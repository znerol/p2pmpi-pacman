package pacifism;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
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

    private final JLabel statusRow;
    private final Timer refreshTimer;

    /**
     * Java swing graphical user interface for pacman model
     */
    public GameGui(ExecutionGovernor governor, KeyListener keyboardController,
            Model model, final Object statusText) {
        super("P2PMPI Pacman");

        GameBoardComponent boardComponent =
                new GameBoardComponent(governor, model);

        setLayout(new BorderLayout());
        add(boardComponent, BorderLayout.CENTER);
        statusRow = new JLabel();
        add(statusRow, BorderLayout.SOUTH);

        pack();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.addKeyListener(keyboardController);

        refreshTimer = new Timer(1000/60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusRow.setText(statusText.toString());
                repaint();
            }
        });
        refreshTimer.start();
    }
}
