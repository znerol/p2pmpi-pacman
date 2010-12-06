package pingpong;

import java.util.ArrayList;
import java.util.List;

import p2pmpi.MpiEventDispatcher;
import p2pmpi.MpiEventSource;
import p2pmpi.mpi.MPI;
import util.EventLogger;
import util.TerminateAfterDuration;
import deism.Event;
import deism.EventCondition;
import deism.EventDispatcher;
import deism.EventDispatcherCollection;
import deism.EventRunloopRecoveryStrategy;
import deism.EventSink;
import deism.EventSinkCollection;
import deism.EventSource;
import deism.EventSourceCollection;
import deism.ExecutionGovernor;
import deism.FailFastRunloopRecoveryStrategy;
import deism.FastForwardRunloop;
import deism.ImmediateExecutionGovernor;
import deism.RealtimeExecutionGovernor;

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

        EventSource[] sources = {
                new BallEventSource(me * 500, 1000, me, other),
                new MpiEventSource(MPI.COMM_WORLD, other, me, 0, governor),
        };

        EventCondition onlyMine = new EventCondition() {
            @Override
            public boolean match(Event event) {
                if (event instanceof BallEvent) {
                    BallEvent ballEvent = (BallEvent)event;
                    return ballEvent.getSender() == me;
                }
                return false;
            }
        };

        List<EventDispatcher> dispatchers = new ArrayList<EventDispatcher>();
        dispatchers.add(new MpiEventDispatcher(MPI.COMM_WORLD, me, other, 0, onlyMine));
        if (me == 0) {
            dispatchers.add(new EventLogger());
        }

        EventRunloopRecoveryStrategy recoveryStrategy =
            new FailFastRunloopRecoveryStrategy();

        EventCondition noSnapshots = new EventCondition() {
            @Override
            public boolean match(Event e) {
                return false;
            }
        };

        FastForwardRunloop runloop = new FastForwardRunloop(governor, termCond,
                recoveryStrategy, noSnapshots);

        runloop.run(new EventSourceCollection(sources),
                new EventSinkCollection(new ArrayList<EventSink>()),
                new EventDispatcherCollection(dispatchers));

        MPI.Finalize();
    }
}
