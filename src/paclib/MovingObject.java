package paclib;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
/**
 * Stellt die Grundfunktionen für ein bewegendes Objekt zur Verfuegung
 * @author Caroline Anklin, Adrian Pauli
 *
 */
abstract class MovingObject {
	protected Coord c;
	protected Board b;
	protected int direction = 0;
	protected boolean[] posMove;
	private Coord startPos;

	protected Shape s;
	protected Color color;

	/**
	 * Erstellt ein neues bewegendes Objekt
	 * @param b Spielbrett
	 * @param c Koordinate
	 */
	public MovingObject(Board b, Coord c) {
		this.c = c;
		this.startPos = new Coord(c);
		this.b = b;
	}
	/**
	 * Erstellt ein neues bewegendes Objekt
	 * @param b Spielbrett
	 * @param x x-Koordinate
	 * @param y y-Koordinate
	 */
	public MovingObject(Board b, int x, int y) {
		this.c = new Coord(x,y,true);
		this.startPos = new Coord(x,y,true);
		this.b = b;
	}

	/**
	 * Wird bei jedem Zug aufgerufen
	 *
	 */
	public abstract void move();

	/**
	 * Gibt eine Referenz auf die Aktuellen Koordianten zurueck
	 * @return Referenz auf Aktuelle Koordinaten.
	 */
	public Coord getCoord() {
		return c;
	}

	/**
	 * Aktualisiert die die neuen Moeglichkeiten des Zuges
	 *
	 */
	protected void getPossibleMoves() {
		posMove = b.getPossibleMoves(c,direction);
	}

	/**
	 * Bewegt das Objekt
	 * @param w Richtung in die bewegt wird
	 */
	protected void go(int w) {
		switch (w) {
		case GamePlay.FIELD_BELOW:
			c.setPointY(c.getPointY() + 1 );
			break;
		case GamePlay.FIELD_ABOVE:
			c.setPointY(c.getPointY() - 1 );
			break;
		case GamePlay.FIELD_RIGHT:
			c.setPointX(c.getPointX() + 1 );
			break;
		case GamePlay.FIELD_LEFT:
			c.setPointX(c.getPointX() - 1 );
			break;
		default:
			break;
		}
	}

	/**
	 * Vergleicht zwei Objekte, ob sie die gleichen Variablen haben
	 * @param mo Zu vergleichendes Objekt
	 * @return ob sie geich sind.
	 */
	public boolean intersect(MovingObject mo) {
		if(Math.pow(c.getPointX()-mo.c.getPointX(),2) + Math.pow(c.getPointY()-mo.c.getPointY(),2) <= Math.pow(GamePlay.GUI_PAC_SIZE,2)) {
			return true;
		}
		return false;

	}

	/**
	 * Setzt die Figur auf die Anfangsposition
	 *
	 */
	public void resetPosition() {
		c.setPointCoord(startPos);
		direction = 0;
	}

	/**
	 * Zeichnet das Objekt
	 * @param g2 graphics Objekt
	 */
	public abstract void paint(Graphics2D g2);
}

