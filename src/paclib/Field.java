package paclib;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**
 * Symbolisiert ein Feld auf dem Spielbrett. Entweder Mauer oder Gang.
 *
 * Zudem muess das Feld noch wissen ob es ein dickes oder ein schmalles Feld ist. Dies ist nur wichtig fuer die Darstellung der Mauern. Schmalle Felder werden weniger dick dargestellt, werden aber theoretisch als gleichgross angeschaut wie die Anderen, da dies keinen Einfluss auf das Spiel hat.
 * @author Caroline Anklin, Adrian Pauli
 *
 */
public class Field {

	private Shape s;
	private Color color;
	private Coord coord;
	private boolean isBigX;
	private boolean isBigY;
	private boolean isFree = true;
	private boolean visited = false;
	private boolean lastState = true;

	/**
	 * Erstellt ein neues Feld
	 * @param free Mauer oder nicht
	 * @param c Koordinate
	 * @param isBigX Ob grosse Breite
	 * @param isBigY Ob schmale Breite
	 */
	public Field(boolean free, Coord c, boolean isBigX, boolean isBigY) {
		coord = c;
 		isFree = free;
 		this.isBigX = isBigX;
 		this.isBigY = isBigY;
 		setShape();
	}

	/**
	 * Erstellt das Shape, welches dargestellt wird.
	 *
	 */
	private void setShape() {
		if(!isFree) {
			int sizeX;
			int sizeY;
			int posX;
			int posY;
			if (isBigX) {
				sizeX = GamePlay.GUI_BIG_FREESPACE*2 + 1;
				posX = coord.getPointX()-GamePlay.GUI_BIG_FREESPACE;
			} else {
				sizeX = GamePlay.GUI_SMALL_FREESPACE*2 + 1;
				posX = coord.getPointX()-GamePlay.GUI_SMALL_FREESPACE;
			}
			if (isBigY) {
				sizeY = GamePlay.GUI_BIG_FREESPACE*2 + 1;
				posY = coord.getPointY()-GamePlay.GUI_BIG_FREESPACE;
			} else {
				sizeY = GamePlay.GUI_SMALL_FREESPACE*2 + 1;
				posY = coord.getPointY()-GamePlay.GUI_SMALL_FREESPACE;
			}
			sizeX *= GamePlay.GUI_SIZE_MULTIPLIER;
			sizeY *= GamePlay.GUI_SIZE_MULTIPLIER;
			posX *= GamePlay.GUI_SIZE_MULTIPLIER;
			posY *= GamePlay.GUI_SIZE_MULTIPLIER;

			s = new Rectangle(posX,posY,sizeX,sizeY);
			color = Color.blue;

		} else if(!visited) {
			int size = GamePlay.GUI_POINT_SIZE*GamePlay.GUI_SIZE_MULTIPLIER;
			int posX = (coord.getPointX())*GamePlay.GUI_SIZE_MULTIPLIER - size/2;
			int posY = (coord.getPointY())*GamePlay.GUI_SIZE_MULTIPLIER - size/2;

			s = new Ellipse2D.Double(posX,posY,size,size);
			color = Color.yellow;
		} else {
			s = null;
		}
	}

	/**
	 * Ob das Feld frei ist
	 * @return Ob Feld frei
	 */
	public boolean isFree() {
		return isFree;
	}

	/**
	 * Ob schon Besucht
	 * @return Ob schon Besucht
	 */
	public boolean isVisited() {
		return visited;
	}

	/**
	 * Setzt das Feld auf Besucht.
	 * @return	Ob geaendert wurde.
	 */
	public boolean setVisited() {
		if(visited) return false;
		this.visited = true;
		setShape();
		return true;
	}

	/**
	 * Setzt das Feld auf nicht Besucht
	 *
	 */
	public void setNotVisited() {
		this.visited = false;
		setShape();
	}

	/**
	 * Zeichnet das Felde
	 * @param g2 Graphics Objekt
	 */
	public void print(Graphics2D g2) {
		if(s != null) {
			g2.setColor(color);
			g2.fill(s);
			if (lastState != visited) {
				g2.draw(s);
				lastState = visited;
			}
		}
	}

	/**
	 * Bestimmt ob die Koordinaten im Mittelpunkt sind
	 * @param coord Zu testende Koordinate
	 * @return ob im Mittelpunkt
	 */
	public boolean isField(Coord coord) {
		return (this.coord.getPointX() == coord.getPointX()) && (this.coord.getPointY() == coord.getPointY());
	}
}
