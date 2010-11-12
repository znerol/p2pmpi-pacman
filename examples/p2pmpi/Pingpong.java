package p2pmpi;

import p2pmpi.mpi.*;

public class Pingpong {
	public static void main(String[] args) {
		int rank, /* size, */ i;

		MPI.Init(args);
		double startTime = MPI.Wtime();

		/* size = MPI.COMM_WORLD.Size(); */
		rank = MPI.COMM_WORLD.Rank();

		int[] n = new int[1];
		int[] r = new int[1];

        for (i=0; i<50; i++) {
            n[0] = i;

    		if(rank == 0) {
                // ping, start with send
			    System.out.println("s " + rank + " send " + n[0]);
                MPI.COMM_WORLD.Send(n, 0, 1, MPI.INT, 1, 0);

                MPI.COMM_WORLD.Recv(r, 0, 1, MPI.INT, 1, 42);
			    System.out.println("r " + rank + " recv " + r[0]);
		    }
            else {
                // pong, start with recv
                MPI.COMM_WORLD.Recv(r, 0, 1, MPI.INT, 0, 0);
			    System.out.println("r " + rank + " recv " + r[0]);

                n[0] = -n[0];
			    System.out.println("s " + rank + " send " + n[0]);
                MPI.COMM_WORLD.Send(n, 0, 1, MPI.INT, 0, 42);
            }
        }

		if(rank == 0) {
			double stopTime = MPI.Wtime();
			System.out.println("Time usage = " + (stopTime - startTime) + " ms");
		}

		MPI.Finalize();
	}
}

