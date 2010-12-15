package wq1;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.BasicConfigurator;

import util.TerminateAfterDuration;
import wqcommon.ClientArrivedEvent;
import wqcommon.ClientArrivedSource;
import wqcommon.PestimisticRunnableClientArrivedSource;

import deism.core.Event;
import deism.core.EventCondition;
import deism.core.EventDispatcher;
import deism.core.EventSink;
import deism.core.EventSinkCollection;
import deism.core.EventSource;
import deism.core.EventSourceCollection;
import deism.run.EventRunloopRecoveryStrategy;
import deism.run.ExecutionGovernor;
import deism.run.FailFastRunloopRecoveryStrategy;
import deism.run.FastForwardRunloop;
import deism.run.ImmediateExecutionGovernor;
import deism.run.RealtimeExecutionGovernor;

public class JobQueueSimulation {
    /**
     * @param args
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
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
        runloop.run(aggSource,
                new EventSinkCollection(new ArrayList<EventSink>()), disp);
    }
}
