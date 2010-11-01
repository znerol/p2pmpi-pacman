package paclib;

import p2pmpi.mpi.MPI;

/**
 * Main-Klasse des Pacman-Spieles
 *
 * @author Caroline Anklin, Adrian Pauli
 *
 */
public class Pacman {
	/**
	 * Main Klasse
	 * @param args nothing
	 */
	public static void main(String[] args) {
		MPI.Init(args);
		GamePlay gp = new GamePlay();
		gp.play();
		MPI.Finalize();
	}

}
