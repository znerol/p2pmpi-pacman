package paclib;

import java.awt.Graphics2D;

/**
 * Spezials
 * @author Caroline Anklin
 *
 */
public interface Special {
	/**
	 * Gibt die Koordianten zurueck
	 * @return Koordianten
	 */
	Coord getCoord();
	/**
	 * Ob von Pacman getroffen
	 * @param c Koordiante
	 * @return Ob getroffen
	 */
	boolean intersects(Coord c);
	/**
	 * Zeichnet das Spezial
	 * @param g2 Grafics Objekt
	 */
	void paint(Graphics2D g2);
	/**
	 * Wird ausgefuehrt wenn getroffen
	 *
	 */
	void act();


}
