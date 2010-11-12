package p2pmpi;
import p2pmpi.mpi.*;

public class Pi {
	public static void main(String[] args) {
		int rank, size, i;
		double PI25DT = 3.141592653589793238462643;
		double h, sum, x;

		MPI.Init(args);
		double startTime = MPI.Wtime();

		size = MPI.COMM_WORLD.Size();
		rank = MPI.COMM_WORLD.Rank();

		int[] n = new int[1];
		double[] mypi = new double[1];
		double[] pi   = new double[1];

		if(rank == 0) {
			n[0] = 1000000; // number of interval
		}

		MPI.COMM_WORLD.Bcast(n, 0, 1, MPI.INT, 0);

		h = 1.0 / (double)n[0];
		sum = 0.0;
		for(i = rank + 1; i <= n[0]; i+= size) {
			x = h * ((double)i - 0.5);
			sum += (4.0/(1.0 + x*x));
		}
		mypi[0] = h * sum;

		MPI.COMM_WORLD.Reduce(mypi, 0, pi, 0, 1, MPI.DOUBLE, MPI.SUM, 0);

		if(rank == 0) {
			System.out.println("Pi is approximately " + pi[0]);
			System.out.println("Error is " + (pi[0] - PI25DT));
			double stopTime = MPI.Wtime();
			System.out.println("Time usage = " + (stopTime - startTime) + " ms");
		}

		MPI.Finalize();
	}
}

