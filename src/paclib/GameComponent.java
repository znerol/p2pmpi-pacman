package paclib;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.JComponent;

/**
 * Darstellende GameComponent.
 * @author Caroline Anklin, Adrian Pauli
 *
 */
public class GameComponent extends JComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8741125567433070665L;
	private Graphics2D g2;
	private Board b;
	private List<Ghost> gt;
	private List<Special> spez;
	private Pac pac;

	@Override
	public void paintComponent(Graphics g) {
		g2 = (Graphics2D) g;
		g2.setColor(Color.black);
		Rectangle bigRectangle = new Rectangle(b.getXsize()*GamePlay.GUI_FIELD_SIZE*GamePlay.GUI_SIZE_MULTIPLIER,b.getYsize()*GamePlay.GUI_FIELD_SIZE*GamePlay.GUI_SIZE_MULTIPLIER);
		g2.fill(bigRectangle);
		g2.draw(bigRectangle);
		b.paint(g2);
		pac.paint(g2);
		for (Special s : spez) {
			s.paint(g2);
		}
		for (Ghost ghost : gt) {
			ghost.paint(g2);
		}

	}

	/**
	 * Erstellt das GameComponent
	 * @param b Spielbrett
	 * @param gt Ghost-Liste
	 * @param spez Spezial-Liste
	 * @param pac Pacman
	 */
	public GameComponent(Board b, List<Ghost> gt, List<Special> spez, Pac pac) {
		super();
		this.b = b;
		this.gt = gt;
		this.pac = pac;
		this.spez = spez;
	}

}
