package pingpong;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;

import p2pmpi.mpi.MPI;
import deism.core.Event;
import deism.core.EventCondition;
import deism.ipc.base.Handler;
import deism.ipc.base.Message;
import deism.p2pmpi.MpiBroadcast;
import deism.p2pmpi.MpiEventSink;
import deism.p2pmpi.MpiEventGenerator;
import deism.p2pmpi.MpiUnicastListener;
import deism.p2pmpi.MpiUnicastEndpoint;
import deism.process.DefaultDiscreteEventProcess;
import deism.process.DefaultProcessBuilder;
import deism.process.DiscreteEventProcess;
import deism.run.LvtListener;
import deism.run.MessageCenter;
import deism.run.NoStateController;
import deism.run.Service;
import deism.run.StateController;
import deism.run.ExecutionGovernor;
import deism.run.Runloop;
import deism.run.ImmediateExecutionGovernor;
import deism.run.RealtimeExecutionGovernor;
import deism.run.StateHistoryController;
import deism.tqgvt.Client;
import deism.tqgvt.GvtMessage;
import deism.tqgvt.GvtMessageFilter;
import deism.tqgvt.Master;
import deism.tqgvt.ReportMessageFilter;

public class Pingpong {

    private static int MASTER_RANK = 2;
    private static int BALL_TAG = 0;
    private static int REPORT_TAG = 1;

    public static class TerminationController implements LvtListener, Handler<Message> {
        private final long limit;
        private final Runloop runloop;
        private long lvt;
        private long gvt;

        public TerminationController(long limit, Runloop runloop) {
            this.limit = limit;
            this.runloop = runloop;
        }

        private void terminateIfLimitReached() {
            if (lvt >= limit && gvt >= limit) {
                runloop.stop();
            }
        }

        @Override
        public void handle(Message message) {
            assert(message instanceof GvtMessage);
            this.gvt = ((GvtMessage)message).getGvt();
            terminateIfLimitReached();
        }

        @Override
        public void update(long lvt) {
            this.lvt = lvt;
            terminateIfLimitReached();
        }

    }
    public static class Player {
        public static DiscreteEventProcess build(ExecutionGovernor governor,
                Service service) {
            final int MY_RANK = MPI.COMM_WORLD.Rank();
            final int PEER_RANK = 1 - MY_RANK;

            DefaultProcessBuilder builder = new DefaultProcessBuilder(service);

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

            return builder.getProcess();
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

        final Service service = new Service();
        service.register(governor);
        final MessageCenter messageCenter = new MessageCenter(governor);

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
            service.register(gvtMessageToClients);
            messageCenter.addEndpoint(gvtMessageToClients,
                    new GvtMessageFilter());

            final MpiUnicastListener gvtReportFromClients =
                    new MpiUnicastListener(MPI.COMM_WORLD, MPI.ANY_SOURCE,
                            REPORT_TAG);
            service.register(gvtReportFromClients);
            messageCenter.addEmitter(gvtReportFromClients);

            final Master tqmaster = new Master(2);
            service.register(tqmaster);
            messageCenter.addEmitter(tqmaster);
            messageCenter.addHandler(tqmaster, new ReportMessageFilter());

            // build process
            // input: -
            process = new DefaultDiscreteEventProcess();
        }
        else {
            // build environment
            // input: governor
            stateController = new StateHistoryController();
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
            service.register(gvtMessageFromMaster);
            messageCenter.addEmitter(gvtMessageFromMaster);

            final MpiUnicastEndpoint gvtReportToMaster =
                    new MpiUnicastEndpoint(MPI.COMM_WORLD, MASTER_RANK,
                            REPORT_TAG);
            service.register(gvtReportToMaster);
            messageCenter.addEndpoint(gvtReportToMaster,
                    new ReportMessageFilter());

            Client tqclient =
                    new Client(MPI.COMM_WORLD.Rank(), 100, stateController);
            service.register(tqclient);
            messageCenter.addEmitter(tqclient);
            messageCenter.addHandler(tqclient, new GvtMessageFilter());

            // build process
            // input: governor, service
            process = Player.build(governor, service);
        }

        stateController.setStateObject(service);

        EventCondition termCond = new EventCondition() {
            @Override
            public boolean match(Event e) {
                return false;
            }
        };

        final Runloop runloop =
                new Runloop(governor, termCond, stateController,
                        snapshotCondition, messageCenter, service);

        TerminationController termController = new TerminationController(1000, runloop);
        messageCenter.addHandler(termController, new GvtMessageFilter());
        service.register(termController);

        runloop.run(process);

        MPI.Finalize();
    }
}
