package pacifism;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;

import p2pmpi.mpi.MPI;

public class PacmanMpi {
    private static int GVT_TQ_SIZE = 100;
    private static int GVT_MASTER_RANK = 2;
    private static int GVT_REPORT_TAG = 1;
    private static int PAC_EVENT_TAG = 2;
    private static double TIME_SCALE = 1000 / 60;

    public static void main(String[] args) {
        args = MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        // we currently support two players
        assert (size == 3);

        // setup log4j, display mpi rank at the beginning of a line
        Layout layout =
                new PatternLayout(MPI.COMM_WORLD.Rank() + " "
                        + PatternLayout.TTCC_CONVERSION_PATTERN);
        Appender appender = new ConsoleAppender(layout);
        BasicConfigurator.configure(appender);

        // setup gvt master and game nodes
        if (rank == GVT_MASTER_RANK) {
            GvtMasterNode node =
                    new GvtMasterNode(MPI.COMM_WORLD, GVT_MASTER_RANK,
                            GVT_REPORT_TAG, size - 1, TIME_SCALE);
            node.run();
        }
        else {
            GameNode node =
                    new GameNode(MPI.COMM_WORLD, GVT_MASTER_RANK,
                            GVT_REPORT_TAG, rank, GVT_TQ_SIZE, TIME_SCALE);
            node.run();
        }

        MPI.Finalize();
    }
}
