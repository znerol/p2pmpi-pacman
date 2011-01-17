package paclib;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**
 * Special dammit man die Ghosts jagen kann.
 * @author Caroline Anklin, Adrian Pauli
 *
 */
public class HappyPill implements Special{

	private Coord c;
	private Board b;
	private Shape s;

	/**
	 * Erstellt ein HappyPill
	 * @param b Spielbrett
	 * @param x x-Koordinate
	 * @param y y-Koordinate
	 */
	public HappyPill(Board b, int x, int y) {
		c = new Coord(x,y,true);
		this.b = b;
	}

	@Override
	public boolean intersects(Coord c) {
		if(Math.pow(this.c.getPointX()-c.getPointX(),2) + Math.pow(this.c.getPointY()-c.getPointY(),2) <= Math.pow((GamePlay.GUI_PAC_SIZE+GamePlay.GUI_POWERUP_SIZE)/2,2)) {
			return true;
		}
		return false;
	}

	@Override
	public void paint(Graphics2D g2) {
		int size = GamePlay.GUI_POWERUP_SIZE*GamePlay.GUI_SIZE_MULTIPLIER;
		int posX = (c.getPointX())*GamePlay.GUI_SIZE_MULTIPLIER - size/2;
		int posY = (c.getPointY())*GamePlay.GUI_SIZE_MULTIPLIER - size/2;

		s = new Ellipse2D.Double(posX,posY,size,size);

		g2.setColor(Color.GREEN);
		g2.fill(s);
		g2.draw(s);


	}

	@Override
	public Coord getCoord() {
		return c;
	}

	@Override
	public void act() {
		b.setHappypill();
	}
}
