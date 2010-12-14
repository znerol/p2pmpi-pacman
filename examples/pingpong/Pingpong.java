package pingpong;

import java.util.ArrayList;
import java.util.List;

import p2pmpi.MpiEventSink;
import p2pmpi.MpiEventSource;
import p2pmpi.mpi.MPI;
import util.EventLogger;
import util.StateHistoryLogger;
import util.TerminateAfterDuration;
import deism.Event;
import deism.EventCondition;
import deism.EventDispatcher;
import deism.EventDispatcherCollection;
import deism.EventRunloopRecoveryStrategy;
import deism.EventSink;
import deism.EventSource;
import deism.EventSourceCollection;
import deism.ExecutionGovernor;
import deism.FastForwardRunloop;
import deism.FilteredEventSink;
import deism.ImmediateExecutionGovernor;
import deism.RealtimeExecutionGovernor;
import deism.StateHistory;
import deism.ThreadedEventSourceRunner;
import deism.TimewarpEventSinkAdapter;
import deism.TimewarpEventSourceAdapter;
import deism.TimewarpRunloopRecoveryStrategy;

public class Pingpong {

    /**
     * @param args
     */
    public static void main(String[] args) {
        MPI.Init(args);
        assert (MPI.COMM_WORLD.Size() == 2);

        /* exit simulation after n units of simulation time */
        EventCondition termCond = new TerminateAfterDuration(10000);

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
//        EventSource mpiEventSource = 
//                new MpiEventSource(MPI.COMM_WORLD, other, 0);
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

        EventCondition noSnapshots = new EventCondition() {
            @Override
            public boolean match(Event e) {
                return false;
            }
        };

        FastForwardRunloop runloop = new FastForwardRunloop(governor, termCond,
                recoveryStrategy, noSnapshots);

        runloop.run(new EventSourceCollection(sources), mpiEventSink,
                new EventDispatcherCollection(dispatchers));

        MPI.Finalize();
    }
}
