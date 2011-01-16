package pacifism;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import model.Direction;

import deism.core.Event;
import deism.core.EventSource;
import deism.core.Stateful;
import deism.run.ExecutionGovernor;

/**
 * AWT key listener and realtime event source for {@link DirectionEvent}.
 */
@Stateful
public class KeyboardController implements KeyListener, EventSource {
    private final int sprite;
    private final ExecutionGovernor governor;
    private Direction nextDirection;
    private DirectionEvent currentEvent;

    public KeyboardController(ExecutionGovernor governor, int sprite) {
        this.governor = governor;
        this.sprite = sprite;
    }

    @Override
    public synchronized Event peek(long currentSimtime) {
        if (nextDirection != null) {
            currentEvent =
                    new DirectionEvent(currentSimtime, sprite, nextDirection);
            nextDirection = null;
        }

        return currentEvent;
    }

    @Override
    public synchronized void remove(Event event) {
        assert (event == currentEvent);
        currentEvent = null;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        synchronized (this) {
            switch (keyCode) {
            case KeyEvent.VK_UP:
                nextDirection = Direction.North;
                break;
            case KeyEvent.VK_DOWN:
                nextDirection = Direction.South;
                break;
            case KeyEvent.VK_LEFT:
                nextDirection = Direction.West;
                break;
            case KeyEvent.VK_RIGHT:
                nextDirection = Direction.East;
                break;
            }
        }

        // tell the runloop that event source wants to get polled.
        governor.resume();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
