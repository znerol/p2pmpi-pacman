package pingpong;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;

import p2pmpi.mpi.MPI;
import util.EventLogger;
import util.StateHistoryLogger;
import util.TerminateAfterDuration;
import deism.core.Event;
import deism.core.EventCondition;
import deism.ipc.base.Handler;
import deism.ipc.base.Message;
import deism.p2pmpi.MpiBroadcastEndpoint;
import deism.p2pmpi.MpiBroadcastListener;
import deism.p2pmpi.MpiEventSink;
import deism.p2pmpi.MpiEventGenerator;
import deism.p2pmpi.MpiUnicastListener;
import deism.p2pmpi.MpiUnicastEndpoint;
import deism.process.DefaultDiscreteEventProcess;
import deism.process.DiscreteEventProcess;
import deism.run.IpcEndpoint;
import deism.run.NoStateController;
import deism.run.StateController;
import deism.run.ExecutionGovernor;
import deism.run.Runloop;
import deism.run.ImmediateExecutionGovernor;
import deism.run.RealtimeExecutionGovernor;
import deism.run.StateHistoryController;
import deism.stateful.DefaultTimewarpDiscreteEventProcess;
import deism.stateful.DefaultTimewarpProcessBuilder;
import deism.tqgvt.Client;
import deism.tqgvt.Master;

public class Pingpong {

    private static int MASTER_RANK = 2;
    private static int BALL_TAG = 0;
    private static int REPORT_TAG = 1;

    public static Handler<Message> buildPlayer(
            DefaultTimewarpDiscreteEventProcess process,
            IpcEndpoint ipcEndpoint, StateController stateController,
            ExecutionGovernor governor) {
        final int MY_RANK = MPI.COMM_WORLD.Rank();
        final int PEER_RANK = 1 - MY_RANK;

        final MpiUnicastEndpoint toMaster = new MpiUnicastEndpoint(MPI.COMM_WORLD,
                MASTER_RANK, REPORT_TAG);
        Client tqclient = new Client(MY_RANK, 100, stateController, toMaster);
        process.addStartable(toMaster);

        final MpiBroadcastListener fromMaster = new MpiBroadcastListener(
                MPI.COMM_WORLD, MASTER_RANK, ipcEndpoint);
        process.addStartable(fromMaster);

        DefaultTimewarpProcessBuilder builder = new DefaultTimewarpProcessBuilder(
                process, tqclient, tqclient);

        builder.add(new BallEventGenerator(MY_RANK * 50, 100, MY_RANK,
                PEER_RANK));
        builder.add(new MpiEventGenerator(MPI.COMM_WORLD, PEER_RANK, BALL_TAG,
                governor));

        EventCondition onlyMine = new EventCondition() {
            @Override
            public boolean match(Event event) {
                if (event instanceof BallEvent) {
                    BallEvent ballEvent = (BallEvent) event;
                    return ballEvent.getSender() == MY_RANK;
                }
                return false;
            }
        };

        builder.add(new MpiEventSink(MPI.COMM_WORLD, PEER_RANK, BALL_TAG),
                onlyMine);

        process.addEventSink(new EventLogger());
        process.addEventDispatcher(new EventLogger());
        process.addEventDispatcher(tqclient);
        process.addStatefulObject(new StateHistoryLogger());

        return tqclient;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        MPI.Init(args);
        assert (MPI.COMM_WORLD.Size() == 3);

        Layout layout = new PatternLayout(MPI.COMM_WORLD.Rank() + " "
                + PatternLayout.TTCC_CONVERSION_PATTERN);
        Appender appender = new ConsoleAppender(layout);
        BasicConfigurator.configure(appender);

        /* exit simulation after n units of simulation time */
        EventCondition termCond = new TerminateAfterDuration(1000);

        String speedString = System.getProperty("simulationSpeed", "0.1");
        double speed = Double.valueOf(speedString).doubleValue();

        ExecutionGovernor governor;
        if (speed > 0) {
            /* run simulation in realtime */
            governor = new RealtimeExecutionGovernor(speed);
        }
        else {
            /* run simulation as fast as possible */
            governor = new ImmediateExecutionGovernor();
        }

        DiscreteEventProcess process;
        StateController stateController;
        Handler<Message> ipcHandler;
        EventCondition snapshotCondition;
        IpcEndpoint ipcEndpoint = new IpcEndpoint(governor);
        if (MPI.COMM_WORLD.Rank() == 2) {
            DefaultDiscreteEventProcess desProcess = new DefaultDiscreteEventProcess();
            stateController = new NoStateController();
            snapshotCondition = new EventCondition() {
                @Override
                public boolean match(Event e) {
                    return false;
                }
            };

            final MpiBroadcastEndpoint toClients = new MpiBroadcastEndpoint(
                    MPI.COMM_WORLD, MASTER_RANK);
            desProcess.addStartable(toClients);

            final MpiUnicastListener fromClients = new MpiUnicastListener(MPI.COMM_WORLD,
                    MPI.ANY_SOURCE, REPORT_TAG, ipcEndpoint);
            desProcess.addStartable(fromClients);

            ipcHandler = new Master(2, toClients);
            process = desProcess;
        }
        else {
            DefaultTimewarpDiscreteEventProcess timewarpProcess = new DefaultTimewarpDiscreteEventProcess();
            process = timewarpProcess;
            stateController = new StateHistoryController(timewarpProcess);

            snapshotCondition = new EventCondition() {
                @Override
                public boolean match(Event e) {
                    return true;
                }
            };

            ipcHandler = buildPlayer(timewarpProcess, ipcEndpoint,
                    stateController, governor);
        }

        Runloop runloop = new Runloop(governor, termCond, stateController,
                snapshotCondition, ipcEndpoint, ipcHandler);
        runloop.run(process);

        MPI.Finalize();
    }
}
