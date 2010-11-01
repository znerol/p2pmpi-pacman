package paclib;

/**
 * Speichert eine Koordiante auf dem Spielfeld.
 *
 * Da die Bewegenden Objekte nicht direkt an die Felder das Spielfeldes gebundens sind, muessten wir hier einge Methoden erstellen, die vom einten Koordiantensystem ins Andere umrechnen. Die bewegenden Objekte bewegen sich im Feinen Koordiantensystem, und die Felder haben nur ein grobes Koordinatensystem. Da die Felder theoretisch als quadratisch angeschaut werden koennen, ist die Umrechnung relativ einfach.
 * @author Caroline Anklin, Adrian Pauli
 *
 */
public class Coord {
	private int x;
	private int y;

	/**
	 * Erstellt eine Koordinate
	 * @param x x-Koordiante
	 * @param y y-Koordiante
	 * @param isField ob Feld-Koordianten oder Feine Koordianten
	 */
	public Coord(int x, int y, boolean isField) {
		if(isField) {
			setFieldCoord(x, y);
		} else {
			setPointCoord(x, y);
		}
	}
	/**
	 * Koordiante Kopieren
	 * @param c Koordiante
	 */
	public Coord(Coord c) {
		setPointCoord(c);
	}
	/**
	 * Gibt die Feinen Koordianten zurueck
	 * @return y-Koordiante
	 */
	public int getPointY() {
		return y;
	}
	/**
	 * Gibt die Feinen Koordianten zurueck
	 * @return x-Koordiante
	 */
	public int getPointX() {
		return x;
	}
	/**
	 * Setzt die Feinen Koordianten
	 * @param x x-Koordiante
	 */
	public void setPointX(int x) {
		this.x = x;
	}
	/**
	 * Setzt die Feinen Koordianten
	 * @param y y-Koordinaten
	 */
	public void setPointY(int y) {
		this.y = y;
	}
	/**
	 * Setzt die Feinen Koordianten
	 * @param x x-Koordianten
	 * @param y y-Koordianten
	 */
	public void setPointCoord(int x, int y) {
		setPointX(x);
		setPointY(y);
	}
	/**
	 * Setzt die Koordinaten auf grund eines Koordianten-Objekt
	 * @param c Koordianten-Objekt
	 */
	public void setPointCoord(Coord c) {
		setPointX(c.x);
		setPointY(c.y);
	}
	/**
	 * Gibt die Feldkoordianten zurueck
	 * @return x-Feldkoordiante
	 */
	public int getFieldX() {
		return x/GamePlay.GUI_FIELD_SIZE;
	}
	/**
	 * Gibt die Feldkoordianten zurueck
	 * @return y-Feldkoordianten
	 */
	public int getFieldY() {
		return y/GamePlay.GUI_FIELD_SIZE;
	}
	/**
	 * Setzt die FeldKoordianten
	 * @param x x-Feldkoordinaten
	 */
	public void setFieldX(int x) {
		this.x = x*GamePlay.GUI_FIELD_SIZE + (GamePlay.GUI_FIELD_SIZE-1) / 2;
	}
	/**
	 * Setzt die Feldkoordianten
	 * @param y y-Feldkoordianten
	 */
	public void setFieldY(int y) {
		this.y = y*GamePlay.GUI_FIELD_SIZE + (GamePlay.GUI_FIELD_SIZE-1) / 2;
	}
	/**
	 * Setzt die Feldkoordianten
	 * @param x x-Feldkoordianten
	 * @param y y-Feldkoordianten
	 */
	public void setFieldCoord(int x, int y) {
		setFieldX(x);
		setFieldY(y);
	}

}
