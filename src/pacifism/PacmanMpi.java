package pacifism;

import java.util.Scanner;

import model.Board;

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
            char[][] strArr = new char[22][21];
            Scanner in =
                    new Scanner("xxxxxxxxxxxxxxxxxxxxx\n"
                            + "x.........x.........x\n"
                            + "xsxxx.xxx.x.xxx.xxxsx\n"
                            + "x.xxx.xxx.x.xxx.xxx.x\n"
                            + "x.........a.........x\n"
                            + "x.xxx.x.xxxxx.x.xxx.x\n"
                            + "x.....x...x...x.....x\n"
                            + "xxxxx.xxx.x.xxx.xxxxx\n"
                            + "xxxxx.x.b.c.d.x.xxxxx\n"
                            + "xxxxx.x.xxxxx.x.xxxxx\n"
                            + "........xxxxx........\n"
                            + "xxxxx.x.xxxxx.x.xxxxx\n"
                            + "xxxxx.x.......x.xxxxx\n"
                            + "xxxxx.x.xxxxx.x.xxxxx\n"
                            + "x.........x.........x\n"
                            + "x.xxx.xxx.x.xxx.xxx.x\n"
                            + "xs..x.....4.....x..sx\n"
                            + "xxx.x.x.xxxxx.x.x.xxx\n"
                            + "x..2..x...x...x..3..x\n"
                            + "x.xxxxxxx.x.xxxxxxx.x\n"
                            + "x.........1.........x\n"
                            + "xxxxxxxxxxxxxxxxxxxxx\n");
            int i = 0;
            while (in.hasNextLine()) {
                strArr[i] = in.nextLine().trim().toCharArray();
                i++;
            }
            Board board = new Board(strArr);
            System.out.print(board);

            GameNode node =
                    new GameNode(MPI.COMM_WORLD, GVT_MASTER_RANK,
                            GVT_REPORT_TAG, rank, GVT_TQ_SIZE, TIME_SCALE,
                            board);
            node.run();
        }

        MPI.Finalize();
    }
}
