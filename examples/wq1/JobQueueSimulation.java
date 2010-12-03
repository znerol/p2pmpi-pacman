package wq1;

import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

import util.TerminateAfterDuration;
import wqcommon.ClientArrivedEvent;
import wqcommon.ClientArrivedSource;
import wqcommon.PestimisticRunnableClientArrivedSource;

import deism.Event;
import deism.EventDispatcher;
import deism.EventCondition;
import deism.EventRunloopRecoveryStrategy;
import deism.EventSource;
import deism.EventSourceCollection;
import deism.ExecutionGovernor;
import deism.FailFastRunloopRecoveryStrategy;
import deism.FastForwardRunloop;
import deism.ImmediateExecutionGovernor;
import deism.RealtimeExecutionGovernor;

public class JobQueueSimulation {
    /**
     * @param args
     */
    public static void main(String[] args) {
        Random rng = new Random(1234);
        
        /* exit simulation after n units of simulation time */
        EventCondition termCond = new TerminateAfterDuration(1000 * 50);
        
        String speedString = System.getProperty("simulationSpeed", "0");
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

        boolean multithread = Boolean.getBoolean("simulationMultithread");
        EventSource clientSource;
        if (multithread) {
            clientSource = new PestimisticRunnableClientArrivedSource(rng,
                    governor, 1000, 1600);
        }
        else {
            clientSource = new ClientArrivedSource(rng, 1000, 1600);
        }
        
        PriorityBlockingQueue<ClientArrivedEvent> jobs =
            new PriorityBlockingQueue<ClientArrivedEvent>();
        /* Define as many customer/clerk sources as you wish */
        EventSource[] sources = {
                clientSource,
                new ClerkSource(jobs),
                new ClerkSource(jobs)
        };
        
        EventSource aggSource = new EventSourceCollection(sources);
        
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
        EventDispatcher disp = new JobAggregator(jobs);
        runloop.run(aggSource, disp);
    }
}
