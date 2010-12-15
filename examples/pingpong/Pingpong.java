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
import deism.adapter.EventSourceStatefulGeneratorAdapter;
import deism.adapter.EventSourceStatelessGeneratorAdapter;
import deism.adapter.FilteredEventSink;
import deism.core.Event;
import deism.core.EventCondition;
import deism.p2pmpi.MpiEventSink;
import deism.p2pmpi.MpiEventGenerator;
import deism.run.EventRunloopRecoveryStrategy;
import deism.run.ExecutionGovernor;
import deism.run.DefaultEventRunloop;
import deism.run.ImmediateExecutionGovernor;
import deism.run.RealtimeExecutionGovernor;
import deism.run.ThreadedEventSourceRunner;
import deism.run.TimewarpRunloopRecoveryStrategy;
import deism.stateful.DefaultTimewarpDiscreteEventProcess;
import deism.stateful.TimewarpEventSinkAdapter;
import deism.stateful.TimewarpEventSource;
import deism.stateful.TimewarpEventSourceAdapter;

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

        final int me = MPI.COMM_WORLD.Rank();
        final int other = 1 - me;

        DefaultTimewarpDiscreteEventProcess process =
            new DefaultTimewarpDiscreteEventProcess();

        process.addEventSource(
                new EventSourceStatelessGeneratorAdapter(
                new BallEventGenerator(me * 50, 100, me, other)));

        ThreadedEventSourceRunner startableMpiEventSource =
            new ThreadedEventSourceRunner(governor,
            new EventSourceStatefulGeneratorAdapter(
            new MpiEventGenerator(MPI.COMM_WORLD, other, 0)));
        process.addStartable(startableMpiEventSource);

        TimewarpEventSource timewarpMpiEventSource = 
            new TimewarpEventSourceAdapter(startableMpiEventSource);
        process.addEventSource(timewarpMpiEventSource);

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

        process.addEventSink(
                new TimewarpEventSinkAdapter(
                new FilteredEventSink(onlyMine,
                new MpiEventSink(MPI.COMM_WORLD, other, 0))));

        process.addEventDispatcher(new EventLogger());
        process.addStatefulObject(new StateHistoryLogger());

        EventRunloopRecoveryStrategy recoveryStrategy =
            new TimewarpRunloopRecoveryStrategy(process);

        EventCondition snapshotAll = new EventCondition() {
            @Override
            public boolean match(Event e) {
                return true;
            }
        };

        DefaultEventRunloop runloop = new DefaultEventRunloop(governor, termCond,
                recoveryStrategy, snapshotAll);

        runloop.run(process);

        MPI.Finalize();
    }
}
