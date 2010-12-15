package wq1;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.BasicConfigurator;

import util.StateHistoryLogger;
import util.TerminateAfterDuration;
import wqcommon.ClientArrivedEvent;
import wqcommon.OptimisticRunnableClientArrivedSource;

import deism.core.Event;
import deism.core.EventCondition;
import deism.core.EventDispatcher;
import deism.core.EventSink;
import deism.core.EventSinkCollection;
import deism.core.EventSource;
import deism.core.EventSourceCollection;
import deism.run.EventRunloop;
import deism.run.EventRunloopRecoveryStrategy;
import deism.run.ExecutionGovernor;
import deism.run.FastForwardRunloop;
import deism.run.RealtimeExecutionGovernor;
import deism.run.TimewarpRunloopRecoveryStrategy;
import deism.stateful.StateHistory;
import deism.stateful.TimewarpEventSource;
import deism.stateful.TimewarpEventSourceAdapter;

public class StupidTimewarpJobQueueSimulation {
    /**
     * @param args
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        Random rng = new Random(1234);
        
        /* exit simulation after n units of simulation time */
        EventCondition termCond = new TerminateAfterDuration(1000 * 50);
        
        String speedString = System.getProperty("simulationSpeed", "1.0");
        double speed = Double.valueOf(speedString).doubleValue();
        
        ExecutionGovernor governor;
        governor = new RealtimeExecutionGovernor(speed);

        EventSource clientSource = new OptimisticRunnableClientArrivedSource(
                rng, governor, speed, 1000, 1600);
        
        PriorityBlockingQueue<ClientArrivedEvent> jobs =
            new PriorityBlockingQueue<ClientArrivedEvent>();
        
        /* Define as many customer/clerk sources as you wish */
        EventSource[] sources = {
                clientSource,
                new ClerkSource(jobs),
                new ClerkSource(jobs)
        };
        
        TimewarpEventSource timewarpSources = 
            new TimewarpEventSourceAdapter(new EventSourceCollection(sources));

        EventCondition snapshotAll = new EventCondition() {
            @Override
            public boolean match(Event e) {
                return true;
            }
        };

        EventDispatcher disp = new JobAggregator(jobs);

        ArrayList<StateHistory<Long>> stateObjects =
            new ArrayList<StateHistory<Long>>();
        stateObjects.add(new StateHistoryLogger());
        stateObjects.add(timewarpSources);

        EventRunloopRecoveryStrategy recoveryStrategy =
            new TimewarpRunloopRecoveryStrategy(stateObjects);

        EventRunloop runloop = new FastForwardRunloop(governor, termCond,
                recoveryStrategy, snapshotAll);
        
        runloop.run(timewarpSources,
                new EventSinkCollection(new ArrayList<EventSink>()), disp);
    }
}
