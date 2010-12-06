package paclib;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;

/**
 * Pacman der Herumlaeuft.
 *
 * Durch den KeyListenerForPac kann man diesen Pacman steuern. Damit man nicht so genau Steuern muss, speichert diese Klasse den letzten Tastendruck und fuehrt ihn aus sobald dies moeglich ist. Zudem wird auch die Richtung in der er laeuft gespeichert, und solange nichts gedrueckt wrid laeuft er immer in diese Richtung wenn er kann.
 * @author Caroline Anklin, Adrian Pauli
 *
 */
public class Pac extends MovingObject {

	private int nextDirection;
	private int lives=3;
	private int points=0;

	private int angle = 0;
	private int steps = 16;

	private GamePlay gp;

	/**
	 * Erstellt den Pacman
	 * @param gp Spielklasse
	 * @param b Spielbrett
	 * @param c Koordiante
	 */
	public Pac(GamePlay gp, Board b, Coord c) {
		super(b, c);
		setShape();
		color = Color.yellow;
		this.gp = gp;
	}
	/**
	 * Erstellt den Pacman
	 * @param gp Spielklasse
	 * @param b Spielbrett
	 * @param x x-Koordiante
	 * @param y y-Koordiante
	 */
	public Pac(GamePlay gp, Board b, int x, int y) {
		super(b, x, y);
		setShape();
		color = Color.yellow;
		this.gp = gp;
	}

	/**
	 * Erstellt die Form von Pacman
	 *
	 */
	public void setShape() {
		int size = GamePlay.GUI_PAC_SIZE*GamePlay.GUI_SIZE_MULTIPLIER;
		int posX = (c.getPointX())*GamePlay.GUI_SIZE_MULTIPLIER - size/2;
		int posY = (c.getPointY())*GamePlay.GUI_SIZE_MULTIPLIER - size/2;

		angle += steps;
		angle %= 200;
		s= new Arc2D.Double();
		((Arc2D.Double) s).setArc(posX,posY,size,size,(90*direction % 360)+Math.abs(angle-100)/2, 360 - Math.abs(angle-100), Arc2D.PIE);

	}

	@Override
	public void move() {
		getPossibleMoves();
		if(posMove[nextDirection]) {
			go(nextDirection);
			direction = nextDirection;
		} else if(posMove[direction]) {
			go(direction);
		}

		// set Field as Visited
		if(b.setVisited(c)) {
			points += GamePlay.POINTS_PER_POINT;
			gp.printPoints(points);
		}
		setShape();
	}

	/**
	 * Setzt die Richtung in die beim naechsten zug gelaufen wird
	 * @param c Wert einer Taste...
	 */
	public void setKey(int c) {
		switch (c) {
		case GamePlay.KEY_RIGHT:
			nextDirection = GamePlay.FIELD_RIGHT;
			break;
		case GamePlay.KEY_BELOW:
			nextDirection = GamePlay.FIELD_BELOW;
			break;
		case GamePlay.KEY_LEFT:
			nextDirection = GamePlay.FIELD_LEFT;
			break;
		case GamePlay.KEY_ABOVE:
			nextDirection = GamePlay.FIELD_ABOVE;
			break;
		default:
			break;
		}
	}

	/**
	 * Gibt die anzahl Leben zurueck
	 * @return Anzahl Leben
	 */
	public int getLives() {
		return lives;
	}
	/**
	 * Gibt die anzahl Punkte zurueck
	 * @return Anzahl Punkte
	 */
	public int getPoints() {
		return points;
	}
	public void paint(Graphics2D g2) {
		g2.setColor(color);
		g2.fill(s);
		g2.draw(s);
	}

	/**
	 * Laesst den Pac sterben. Nimmt ihm ein Leben
	 * @return Ob er noch Leben hat
	 */
	public boolean die() {
		lives--;
		if(lives == 0) {
			return false;
		}
		return true;

	}
	/**
	 * Setzt den Pacman an den Ursprung zurueck
	 *
	 */
	public void reset() {
		lives = 3;
		points = 0;
	}
	/**
	 * Zaehlt Punkte dazu
	 * @param i Anzahl Punkte
	 */
	public void addPoints(int i) {
		points += i;
	}
}
