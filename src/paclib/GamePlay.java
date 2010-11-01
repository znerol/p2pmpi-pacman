package paclib;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Hier wird der Spielablauf und die Initialisierung organisiert.
 *
 * Beim erstellen des Spieles wird aus einer externen Textdatei das Spielbrett generiert. Waehrend dem Spielablauf wird nach jeder Spielrunde getestet ob der Pacman von einem Geist gefressen wurde. Wenn dies der Fall ist wird die Runde beendet und wenn es noch Leben hat, werden alle bewegten Objekte an den Ursprung zurueck gesetzt.
 * @author Caroline Anklin, Adrian Pauli
 *
 */
public class GamePlay extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -540095004290839535L;
	/**
	 * Feld Rechts
	 */
	public static final int FIELD_RIGHT	= 0;
	/**
	 * Feld Unten
	 */
	public static final int FIELD_BELOW	= 3;
	/**
	 * Feld Links
	 */
	public static final int FIELD_LEFT	= 2;
	/**
	 * Feld Links
	 */
	public static final int FIELD_ABOVE	= 1;

	/**
	 * Taste Rechts
	 */
	public static final int KEY_RIGHT	= 39;
	/**
	 * Taste Unten
	 */
	public static final int KEY_BELOW	= 40;
	/**
	 * Taste Links
	 */
	public static final int KEY_LEFT	= 37;
	/**
	 * Taste Oben
	 */
	public static final int KEY_ABOVE	= 38;

	/**
	 * Kleiner Gelber Punkt
	 */
	public static final int GUI_POINT_SIZE 		= 2;
	/**
	 * Pacman Groesse
	 */
	public static final int GUI_PAC_SIZE 		= 10;
	/**
	 * Ghost Groesse
	 */
	public static final int GUI_GHOST_SIZE 		= 10;
	/**
	 * Powerup Groesse
	 */
	public static final int GUI_POWERUP_SIZE 		= 5;


	/**
	 * Halbe Groesse der Wand
	 */
	public static final int GUI_SMALL_FREESPACE		= 2;
	/**
	 * Halbe Groesse des Durchganges
	 */
	public static final int GUI_BIG_FREESPACE		= 5;
	/**
	 * Feld Groesse
	 */
	public static final int GUI_FIELD_SIZE			= GUI_SMALL_FREESPACE+GUI_BIG_FREESPACE+1;
	/**
	 * Streckfaktor
	 */
	public static final int GUI_SIZE_MULTIPLIER 	= 3;
	/**
	 * Sleep Time
	 */
	public static final int SPEED					= 30;

	/**
	 * Punkte die gegeben werden
	 */
	public static final int POINTS_PER_POINT		= 3;

	/**
	 * Zeit wo die HappyPill wirkt
	 */
	public static final int SPECIAL_HAPPYPILL_TIME	= 500;


	private String file = "world1";

	private Board b;
	private List<Ghost> gt;
	private Pac pac;
	private GameComponent gc;
	private KeyListener kl;
	private JLabel lblPoints;
	private JLabel lblLives;
	private ArrayList<Special> spez = new ArrayList<Special>();

	/**
	 * Standart Konstruktor
	 *
	 */
	public GamePlay() {
		super("Super Pacman!!");

		init(file);
		gc = new GameComponent(b,gt,spez,pac);
		kl = new KeyListenerForPac(pac);

		this.setLayout(new BorderLayout());
		this.add(gc, BorderLayout.CENTER);
		JPanel pnl = new JPanel();
		pnl.setLayout(new GridLayout(0,2));
		lblPoints = new JLabel("Points: 0");
		lblLives = new JLabel("Lives: 3");
		pnl.add(lblPoints);
		pnl.add(lblLives);
		this.add(pnl, BorderLayout.SOUTH);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.addKeyListener(kl);

		this.setSize(new Dimension(b.getXsize()*GUI_FIELD_SIZE*GUI_SIZE_MULTIPLIER,(b.getYsize() + 1)*GUI_FIELD_SIZE*GUI_SIZE_MULTIPLIER+20));

		this.setVisible(true);
	}

	/**
	 * Schreibt die Punkte des Pacmans.
	 * @param points Punkte die geschrieben werden sollen.
	 */
	public void printPoints(int points) {
		lblPoints.setText("Points: " + points);
		lblPoints.repaint();
	}

	/**
	 * Schreibt die anzahl Leben des Pacmans.
	 * @param lives Leben die geschrieben werden sollen.
	 */
	public void printLives(int lives) {
		lblLives.setText("Lives: " + lives);
		lblLives.repaint();
	}

	/**
	 * Eroeffnet das Spielfeld aufgrund von einer Weltdatei.
	 * @param file Weltdatei
	 */
	private void init(String file){

		try {
			FileReader reader = new FileReader(file);
			gt = new ArrayList<Ghost>();
			Scanner in = new Scanner(reader);
			ArrayList<String> strArr = new ArrayList<String>();
			while (in.hasNextLine()) {
				strArr.add(in.nextLine().trim());
			}
			in.close();
			Field[][] sb = new Field[strArr.get(0).length()][strArr.size()];
			b = new Board(sb);
			boolean isBigY = true;
			for(int i=0; i<strArr.size(); i++) {
				isBigY = !isBigY;
				boolean isBigX = true;
				for(int j=0; j<strArr.get(i).length();j++) {
					Coord c = new Coord(j,i,true);
					char todo = strArr.get(i).charAt(j);
					isBigX = !isBigX;
					if(todo == 'x') {
						sb[j][i] = new Field(false,c,isBigX,isBigY);
					} else {
						if(todo == 'g') {
							gt.add(new Ghost(b,j,i));
						} else if (todo=='p') {
							pac = new Pac(this, b, j,i);
						} else if (todo=='s') {
							spez.add(new HappyPill(b,j,i));
						}
						sb[j][i] = new Field(true,c,isBigX,isBigY);
						b.countVisited();

					}
				}
			}

			for(Ghost ghost: gt) {
				ghost.setPac(pac);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Beginnt das Spiel und führt hindurch.
	 *
	 */
	public void play(){
		boolean win = false;
		int choice = JOptionPane.YES_OPTION;
		while (choice == JOptionPane.YES_OPTION) {
			do {
				while(!testCollision() && !win) {
					for (int i = 0; i < gt.size(); i++) gt.get(i).move();
					pac.move();
					gc.repaint();

					///   //////  /////////////  //////////
					printPoints(pac.getPoints());
					win = b.allVisited();

					for (int i = 0; i < spez.size(); i++) {
						if(spez.get(i).intersects(pac.getCoord())) {
							spez.get(i).act();
							spez.remove(i);
						}
					}
					b.minHappyPill();
					try {
						Thread.sleep(SPEED);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				printLives(pac.getLives()-1);

				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				pac.resetPosition();
				for (int i = 0; i < gt.size(); i++) gt.get(i).resetPosition();
			} while (!win && pac.die());
			if(win) {
				System.out.println("You Won!");
				choice = JOptionPane.showConfirmDialog(gc, "you won!! :-) try again?", "Game over", JOptionPane.YES_NO_OPTION);
			}
			else {
				System.out.println("You Lost!!!");
				choice = JOptionPane.showConfirmDialog(gc, "you lost :-( try again?", "Game over", JOptionPane.YES_NO_OPTION);
			}
			if (choice == JOptionPane.YES_OPTION) {
				win = false;
				reset();
			}
			else {
				System.exit(0);
			}
		}
	}

	/**
	 * Setzt alle Objekte an den Afangsstandort.
	 *
	 */
	public void reset() {
		b.reset();
		pac.reset();
		printLives(pac.getLives());
		printPoints(pac.getPoints());
	}

	/**
	 * Testet ob ein Geist mit dem Pacman kollidiert
	 * @return ob Kollidiert
	 */
	public boolean testCollision() {
		boolean intersects;
		for (int i = 0; i < gt.size(); i++) {
			intersects = gt.get(i).intersect(pac);
			if(intersects) {
				if(!b.getHappypill())
					return true;
				else {
					gt.get(i).resetPosition();
					pac.addPoints(20);
				}
			}
		}
		return false;
	}

}