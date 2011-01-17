package paclib;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * KeyListener um Pacman zu steuern
 * @author Caroline Anklin, Adrian Pauli
 *
 */
public class KeyListenerForPac implements KeyListener{
	private Pac pac;

	/**
	 * Erstellt einen Key-Listener
	 * @param pac Pacman
	 */
	public KeyListenerForPac(Pac pac) {
		this.pac = pac;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		pac.setKey(arg0.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
