package pingpong;

import java.util.ArrayDeque;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;

import p2pmpi.mpi.MPI;
import util.TerminateAfterDuration;
import deism.core.Event;
import deism.core.EventCondition;
import deism.core.Startable;
import deism.ipc.base.EventExporter;
import deism.ipc.base.EventImporter;
import deism.p2pmpi.MpiBroadcast;
import deism.p2pmpi.MpiEventSink;
import deism.p2pmpi.MpiEventGenerator;
import deism.p2pmpi.MpiUnicastListener;
import deism.p2pmpi.MpiUnicastEndpoint;
import deism.process.DefaultDiscreteEventProcess;
import deism.process.DiscreteEventProcess;
import deism.run.MessageCenter;
import deism.run.LvtListener;
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

    public static class Player {
        public static void build(DefaultTimewarpProcessBuilder builder,
                ExecutionGovernor governor) {
            final int MY_RANK = MPI.COMM_WORLD.Rank();
            final int PEER_RANK = 1 - MY_RANK;

            // my ball event source
            builder.add(new BallEventGenerator(MY_RANK * 50, 100, MY_RANK,
                    PEER_RANK));

            // balls from the peer player
            builder.add(new MpiEventGenerator(MPI.COMM_WORLD, PEER_RANK,
                    BALL_TAG, governor));

            // balls going to the peer
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
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        MPI.Init(args);
        assert (MPI.COMM_WORLD.Size() == 3);

        Layout layout =
                new PatternLayout(MPI.COMM_WORLD.Rank() + " "
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
        EventCondition snapshotCondition;

        final ArrayDeque<Startable> startables = new ArrayDeque<Startable>();
        final MessageCenter messageCenter = new MessageCenter(governor);
        final LvtListener lvtListener;

        // build tq master
        if (MPI.COMM_WORLD.Rank() == 2) {
            // build environment
            // input: governor
            stateController = new NoStateController();
            snapshotCondition = new EventCondition() {
                @Override
                public boolean match(Event e) {
                    return false;
                }
            };

            // gvt
            // input: messageCenter
            final MpiBroadcast gvtMessageToClients =
                    new MpiBroadcast(MPI.COMM_WORLD, MASTER_RANK);
            startables.add(gvtMessageToClients);
            final MpiUnicastListener gvtReportFromClients =
                    new MpiUnicastListener(MPI.COMM_WORLD, MPI.ANY_SOURCE,
                            REPORT_TAG);
            startables.add(gvtReportFromClients);
            lvtListener = null; // dangerous!

            final Master tqmaster = new Master(2);
            tqmaster.setEndpoint(gvtMessageToClients);
            gvtReportFromClients.setEndpoint(messageCenter);
            messageCenter.addHandler(tqmaster);

            // build process
            // input: -
            DefaultDiscreteEventProcess desProcess =
                    new DefaultDiscreteEventProcess();

            process = desProcess;
        }
        else {
            // build environment
            // input: governor
            StateHistoryController shc = new StateHistoryController();
            stateController = shc;
            snapshotCondition = new EventCondition() {
                @Override
                public boolean match(Event e) {
                    return true;
                }
            };

            // gvt
            // input: messageCenter, stateController
            final MpiBroadcast gvtMessageFromMaster =
                    new MpiBroadcast(MPI.COMM_WORLD, MASTER_RANK);
            startables.add(gvtMessageFromMaster);
            final MpiUnicastEndpoint gvtReportToMaster =
                    new MpiUnicastEndpoint(MPI.COMM_WORLD, MASTER_RANK,
                            REPORT_TAG);
            startables.add(gvtReportToMaster);
            Client tqclient =
                    new Client(MPI.COMM_WORLD.Rank(), 100, stateController);
            tqclient.setEndpoint(gvtReportToMaster);
            gvtMessageFromMaster.setEndpoint(messageCenter);
            messageCenter.addHandler(tqclient);
            lvtListener = tqclient;

            final EventExporter exporter = tqclient;
            final EventImporter importer = tqclient;

            // build process
            // input: governor, importer, exporter (gvtclient)
            DefaultTimewarpDiscreteEventProcess timewarpProcess =
                    new DefaultTimewarpDiscreteEventProcess();
            shc.setStateObject(timewarpProcess);

            DefaultTimewarpProcessBuilder builder =
                    new DefaultTimewarpProcessBuilder(timewarpProcess,
                            importer, exporter);
            Player.build(builder, governor);

            process = timewarpProcess;
        }

        Runloop runloop =
                new Runloop(governor, termCond, stateController,
                        snapshotCondition, messageCenter, lvtListener);

        for (Startable startable : startables) {
            startable.start(0);
        }

        runloop.run(process);

        for (Startable startable : startables) {
            startable.stop(0);
        }

        MPI.Finalize();
    }
}
