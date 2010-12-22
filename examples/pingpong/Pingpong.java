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
import deism.p2pmpi.MpiEventSink;
import deism.p2pmpi.MpiEventGenerator;
import deism.p2pmpi.MpiListener;
import deism.p2pmpi.MpiEndpoint;
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

    public static Handler<Message> buildPlayer(
            DefaultTimewarpDiscreteEventProcess process,
            IpcEndpoint ipcEndpoint, StateController stateController,
            ExecutionGovernor governor) {
        final int me = MPI.COMM_WORLD.Rank();
        final int other = 1 - me;

        final MpiEndpoint toMaster = new MpiEndpoint(MPI.COMM_WORLD, 2, 1);
        Client tqclient = new Client(me, 100, stateController, toMaster);
        process.addStartable(toMaster);
        final MpiListener fromMaster = new MpiListener(MPI.COMM_WORLD, 2, 1,
                ipcEndpoint);
        process.addStartable(fromMaster);

        DefaultTimewarpProcessBuilder builder = new DefaultTimewarpProcessBuilder(
                process, tqclient, tqclient);

        builder.add(new BallEventGenerator(me * 50, 100, me, other));
        builder.add(new MpiEventGenerator(MPI.COMM_WORLD, other, 0, governor));

        EventCondition onlyMine = new EventCondition() {
            @Override
            public boolean match(Event event) {
                if (event instanceof BallEvent) {
                    BallEvent ballEvent = (BallEvent) event;
                    return ballEvent.getSender() == me;
                }
                return false;
            }
        };

        builder.add(new MpiEventSink(MPI.COMM_WORLD, other, 0), onlyMine);

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

            final MpiEndpoint toClients = new MpiEndpoint(MPI.COMM_WORLD, 0, 1);
            desProcess.addStartable(toClients);
            final MpiListener fromClients = new MpiListener(MPI.COMM_WORLD,
                    MPI.ANY_SOURCE, 1, ipcEndpoint);
            desProcess.addStartable(fromClients);
            final Master tqmaster = new Master(2, toClients);
            ipcHandler = tqmaster;
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
