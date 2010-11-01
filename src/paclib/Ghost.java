package paclib;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Representiert einen Geist.
 *
 * Die Geister laufen meistens einfach nach Zufall herum.
 * Sie gehen jedoch nur rueckwaerts wenn sie in eine Sackgasse laufen.
 * Zusaetzlich schauen sie ob der Pacman entweder die gleichen X-Koordinaten oder Y-Koordinaten hat.
 * Wenn dies der Fall ist, gehen sie in die Richtung wo er sich befindent wenn es Moeglich ist.
 * Wenn sich das Spiel im HappyPill-Modus ist, streichen sie diese Richtung aus den Moeglichkeiten raus.
 *
 * @author Caroline Anklin, Adrian Pauli
 *
 */
public class Ghost extends MovingObject {

	private Pac pac;
	private boolean happypillslower = false;

	/**
	 * Erstellt einen Geist
	 * @param b Spielbrett
	 * @param c Koordianten
	 */
	public Ghost(Board b, Coord c) {
		super(b, c);
		setShape();
		color = Color.red;
	}

	/**
	 * Erstellt einen Geist
	 * @param b Spielbrett
	 * @param x x-Koordianten
	 * @param y y-Koordianten
	 */
	public Ghost(Board b, int x, int y) {
		super(b, x, y);
		setShape();
		color = Color.red;
	}

	/**
	 * Setzt den Pacman
	 * @param pac Pacman
	 */
	public void setPac(Pac pac) {
		this.pac = pac;
	}

	/**
	 * Erstellt das Shape des Geistes
	 *
	 */
	private void setShape() {
		if(b.getHappypill()) color = Color.magenta;
		else color = Color.red;
		int size = GamePlay.GUI_GHOST_SIZE*GamePlay.GUI_SIZE_MULTIPLIER;
		int posX = (c.getPointX())*GamePlay.GUI_SIZE_MULTIPLIER - size/2;
		int posY = (c.getPointY())*GamePlay.GUI_SIZE_MULTIPLIER - size/2;

		s = new Ellipse2D.Double(posX,posY,size,size);
	}

	@Override
	public void move() {
		if(b.getHappypill()) {
			if(happypillslower) {
				happypillslower = false;
				return;
			} else {
				happypillslower = true;
			}
		}
		getPossibleMoves();

		// Umschreiben des Array in eine Arraylist die nur die true-Faele enthaelt.
		List<Integer> posMoveList = new ArrayList<Integer>();
		for(int i=0;i<posMove.length;i++){
			// ohne retourweg
			if(posMove[i] && (i+2)%4 != direction) {
				posMoveList.add((Integer) i);
			}
		}

		// Keine Abzweigung und keine Sackgasse
		if(posMove[direction] && posMoveList.size() == 1) {
			go(direction);
		}
		// Zurueckgehen wenn Sackgasse
		else if (posMoveList.size() == 0) {
			direction = (direction + 2)%4;
			go(direction);
		}

		// Mit Abzweigung oder Sackgasse
		else {
			boolean seen = false;
			int i = 0;
			int toremove = -1;
			for(Integer integr: posMoveList) {
				if(integr.intValue() == GamePlay.FIELD_ABOVE && pac.c.getFieldX() == c.getFieldX() && pac.c.getFieldY() < c.getFieldY()) {
					seen = true;
				} else if(integr.intValue() == GamePlay.FIELD_BELOW && pac.c.getFieldX() == c.getFieldX() && pac.c.getFieldY() > c.getFieldY()) {
					seen = true;
				} else if(integr.intValue() == GamePlay.FIELD_LEFT && pac.c.getFieldY() == c.getFieldY() && pac.c.getFieldX() < c.getFieldX()) {
					seen = true;
				} else if(integr.intValue() == GamePlay.FIELD_RIGHT && pac.c.getFieldY() == c.getFieldY() && pac.c.getFieldX() > c.getFieldX()) {
					seen = true;
				}

				if(seen && !b.getHappypill()) {
					direction = integr.intValue();
					go(direction);
					setShape();
					return;
				}
				else if (b.getHappypill()) {
					toremove = i;
				}
				i++;
			}
			if(toremove > 0) {
				posMoveList.remove(toremove);
			}

			Random rand = new Random();
			// wählt per Zufall den Weg aus den man gehen will
			direction = posMoveList.get(rand.nextInt(posMoveList.size())).intValue();
			go(direction);
		}

		setShape();
	}

	@Override
	public void paint(Graphics2D g2) {


	     //Graphics2D g2 = (Graphics2D) g;

	     int posX = (c.getPointX()-GamePlay.GUI_GHOST_SIZE/2)*GamePlay.GUI_SIZE_MULTIPLIER;
	     int posY = (c.getPointY())*GamePlay.GUI_SIZE_MULTIPLIER;

	     int size = GamePlay.GUI_GHOST_SIZE*GamePlay.GUI_SIZE_MULTIPLIER;

	     int mouthBottom = posY+size/3;
	     int mouthTop = posY+size/6;
	     int mouthDistance = size/7;


	     // Construct a rectangle and draw it
	     Rectangle box = new Rectangle(posX, posY, size, size/2);
	     Ellipse2D.Double e = new Ellipse2D.Double(posX, posY-size/2, size, size);

	     Ellipse2D.Double eye1 = new Ellipse2D.Double(posX+size/5,posY-size/4, size/4, size/4);
	     Ellipse2D.Double eye2 = new Ellipse2D.Double(posX+3*size/5,posY-size/4, size/4, size/4);

	     g2.setColor(color);
	     g2.fill(box);
	     g2.fill(e);

	     g2.setColor(Color.black);
	     g2.fill(eye1);
	     g2.fill(eye2);


	     int pointX[] = { posX+mouthDistance, posX+mouthDistance*2,posX+mouthDistance*3, posX+mouthDistance*4, posX+mouthDistance*5,
	posX+mouthDistance*6 };
	     int pointY[] = { mouthBottom, mouthTop, mouthBottom, mouthTop,
	mouthBottom, mouthTop };
	     g2.drawPolyline(pointX, pointY, pointX.length);
	}
}
