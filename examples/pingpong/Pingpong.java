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
import deism.core.Message;
import deism.core.MessageHandler;
import deism.p2pmpi.MpiEventSink;
import deism.p2pmpi.MpiEventGenerator;
import deism.p2pmpi.MpiMessageSender;
import deism.run.StateController;
import deism.run.ExecutionGovernor;
import deism.run.DefaultEventRunloop;
import deism.run.ImmediateExecutionGovernor;
import deism.run.RealtimeExecutionGovernor;
import deism.run.StateHistoryController;
import deism.stateful.DefaultTimewarpDiscreteEventProcess;
import deism.stateful.DefaultTimewarpProcessBuilder;
import deism.tqgvt.Client;

public class Pingpong {

    /**
     * @param args
     */
    public static void main(String[] args) {
        MPI.Init(args);
        assert (MPI.COMM_WORLD.Size() == 2);

        Layout layout = new PatternLayout(MPI.COMM_WORLD.Rank() + " " + PatternLayout.TTCC_CONVERSION_PATTERN);
        Appender appender = new ConsoleAppender(layout);
        BasicConfigurator.configure(appender);

        /* exit simulation after n units of simulation time */
        EventCondition termCond = new TerminateAfterDuration(1000);

        String speedString = System.getProperty("simulationSpeed", "1.0");
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

        final int me = MPI.COMM_WORLD.Rank();
        final int other = 1 - me;

        DefaultTimewarpDiscreteEventProcess process =
            new DefaultTimewarpDiscreteEventProcess();
        StateController stateController =
            new StateHistoryController(process);

        final MpiMessageSender master = new MpiMessageSender(MPI.COMM_WORLD, other, 1);
        Client tqclient = new Client(me, 100, stateController, master);
        process.addStartable(master);

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

        EventCondition snapshotAll = new EventCondition() {
            @Override
            public boolean match(Event e) {
                return true;
            }
        };

        MessageHandler messageHandler = new MessageHandler() {
            @Override
            public void handle(Message item) {
            }
        };

        DefaultEventRunloop runloop = new DefaultEventRunloop(governor, termCond,
                stateController, snapshotAll, messageHandler);

        runloop.run(process);

        MPI.Finalize();
    }
}
