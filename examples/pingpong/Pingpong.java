package pingpong;

import java.util.ArrayList;
import java.util.List;

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
import deism.core.EventDispatcher;
import deism.core.EventDispatcherCollection;
import deism.core.EventSink;
import deism.core.EventSource;
import deism.core.EventSourceCollection;
import deism.core.FilteredEventSink;
import deism.p2pmpi.MpiEventSink;
import deism.p2pmpi.MpiEventSource;
import deism.run.EventRunloopRecoveryStrategy;
import deism.run.ExecutionGovernor;
import deism.run.FastForwardRunloop;
import deism.run.ImmediateExecutionGovernor;
import deism.run.RealtimeExecutionGovernor;
import deism.run.ThreadedEventSourceRunner;
import deism.run.TimewarpRunloopRecoveryStrategy;
import deism.stateful.StateHistory;
import deism.stateful.TimewarpEventSinkAdapter;
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

        EventSource mpiEventSource = new ThreadedEventSourceRunner(governor,
                new MpiEventSource(MPI.COMM_WORLD, other, 0));
        EventSource[] sources = { new BallEventSource(me * 50, 100, me, other),
                mpiEventSource, };

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

        EventSink mpiEventSink = new FilteredEventSink(onlyMine,
                new MpiEventSink(MPI.COMM_WORLD, other, 0));

        List<EventDispatcher> dispatchers = new ArrayList<EventDispatcher>();
        if (me == 0) {
            dispatchers.add(new EventLogger());
        }

        ArrayList<StateHistory<Long>> stateObjects = new ArrayList<StateHistory<Long>>();
        stateObjects.add(new StateHistoryLogger());
        stateObjects.add(new TimewarpEventSinkAdapter(mpiEventSink));
        stateObjects.add(new TimewarpEventSourceAdapter(mpiEventSource));

        EventRunloopRecoveryStrategy recoveryStrategy = new TimewarpRunloopRecoveryStrategy(
                stateObjects);

        EventCondition snapshotAll = new EventCondition() {
            @Override
            public boolean match(Event e) {
                return true;
            }
        };

        FastForwardRunloop runloop = new FastForwardRunloop(governor, termCond,
                recoveryStrategy, snapshotAll);

        runloop.run(new EventSourceCollection(sources), mpiEventSink,
                new EventDispatcherCollection(dispatchers));

        MPI.Finalize();
    }
}
