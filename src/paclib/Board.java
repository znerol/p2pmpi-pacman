package paclib;

import java.awt.Graphics2D;

/**
 * Das Spielbrett auf dem gespielt wird.
 *
 * Es ist ein Array mit allen Feldern vorhanden. Zudem kann man das Spielbrett anfragen wellche Bewegungen moeglich sind.
 * @author Caroline Anklin, Adrian Pauli
 *
 */
public class Board {
	private Field[][] sb;
	private int notVisited = 0;
	private boolean happypill = false;
	private int happypillTime = 0;

	/**
	 * Standart Konstruktor
	 * @param sb ein Field Array das dem Spielbrette entspricht
	 */
	public Board(Field[][] sb) {;
		this.sb = sb;
	}

	/**
	 * Berechnet die moeglichen Zuege.
	 * @param coord Koordinaten wo sich das Moving object befindet.
	 * @param direction Richtung wo er Laeuft
	 * @return ein Boolean Array das jede Richtung abdeckt.
	 */
	public boolean[] getPossibleMoves(Coord coord, int direction) {

		Field field;
		boolean[] freeField = new boolean[4];

		if(!sb[coord.getFieldX()][coord.getFieldY()].isField(coord)) {
			freeField[direction] = true;
			freeField[(direction + 1)%4] = false;
			freeField[(direction + 2)%4] = true;
			freeField[(direction + 3)%4] = false;
		} else {

			// rechts
			field = sb[coord.getFieldX()+1][coord.getFieldY()];
			freeField[GamePlay.FIELD_RIGHT] = field.isFree();

			// oben
			field = sb[coord.getFieldX()][coord.getFieldY()+1];
			freeField[GamePlay.FIELD_BELOW] = field.isFree();

			// links
			field = sb[coord.getFieldX()-1][coord.getFieldY()];
			freeField[GamePlay.FIELD_LEFT] = field.isFree();

			// unten
			field = sb[coord.getFieldX()][coord.getFieldY()-1];
			freeField[GamePlay.FIELD_ABOVE] = field.isFree();
		}
		return freeField;
	}

	/**
	 * Setzt ein Feld auf besucht.
	 * @param c Koordianten des Feldes
	 * @return ob schon besucht.
	 */
	public boolean setVisited(Coord c) {
		if(sb[c.getFieldX()][c.getFieldY()].setVisited()) {
			notVisited--;
			return true;
		}
		return false;
	}

	/**
	 * Testet ob schon alle Felder besucht wurden.
	 * @return Ob alle Felder besucht.
	 */
	public boolean allVisited() {
		if(notVisited == 0) return true;
		return false;
	}

	/**
	 * Setzt ein Feld als besucht.
	 *
	 */
	public void countVisited() {
		notVisited++;
	}

	/**
	 * Gibt die Hoehe des ganzen Brettes zurueck.
	 * @return Hoehe des Brettes
	 */
	public int getYsize() {
		return sb[0].length;
	}

	/**
	 * Gibt die Breite des ganzen Brettes zurueck
	 * @return Breite des Brettes
	 */
	public int getXsize() {
		return sb.length;
	}

	/**
	 * Zeichnet das Spielbrett.
	 * @param g2 Graphics Objekt
	 */
	public void paint(Graphics2D g2) {
		for (int i = 0; i < sb.length; i++) {
			for (int j = 0; j < sb[i].length; j++) {
				sb[i][j].print(g2);
			}
		}
	}

	/**
	 * Setzt das Brett zurueck
	 *
	 */
	public void reset() {
		notVisited = 0;
		for(int i=0; i<sb.length;i++) {
			for(int j=0;j<sb[0].length;j++) {
				if (sb[i][j].isFree()) {
					notVisited++;
					sb[i][j].setNotVisited();
				}
			}
		}
	}

	/**
	 * Wechselt in den HappyPill-Mode
	 *
	 */
	public void setHappypill() {
		happypill = true;
		happypillTime = GamePlay.SPECIAL_HAPPYPILL_STEPS;
	}

	/**
	 * Gibt zurueck ob man sich im HappyPill-Mode befindet
	 * @return in HappyPill-Mode
	 */
	public boolean getHappypill() {
		return happypill;
	}

	/**
	 * Zaehlt die Zeit zurueck bis der HappyPill-Mode fertig ist.
	 *
	 */
	public void minHappyPill() {
		if(happypill) {
			happypillTime--;
			if(happypillTime < 1) {
				happypill = false;
			}
		}
	}

}
