package wq1;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

import util.StateHistoryLogger;
import util.TerminateAfterDuration;
import wqcommon.ClientArrivedEvent;
import wqcommon.OptimisticRunnableClientArrivedSource;

import deism.Event;
import deism.EventDispatcher;
import deism.EventCondition;
import deism.EventRunloop;
import deism.EventRunloopRecoveryStrategy;
import deism.EventSource;
import deism.EventSourceCollection;
import deism.ExecutionGovernor;
import deism.FastForwardRunloop;
import deism.RealtimeClock;
import deism.RealtimeExecutionGovernor;
import deism.StateHistory;
import deism.TimewarpEventSource;
import deism.TimewarpRunloopRecoveryStrategy;
import deism.TimewarpEventSourceAdapter;

public class StupidTimewarpJobQueueSimulation {
    /**
     * @param args
     */
    public static void main(String[] args) {
        Random rng = new Random(1234);
        
        /* exit simulation after n units of simulation time */
        EventCondition termCond = new TerminateAfterDuration(1000 * 50);
        
        String speedString = System.getProperty("simulationSpeed", "1.0");
        double speed = Double.valueOf(speedString).doubleValue();
        
        ExecutionGovernor governor;
        RealtimeClock clock = new RealtimeClock(speed);
        governor = new RealtimeExecutionGovernor(clock);            

        EventSource clientSource;
        OptimisticRunnableClientArrivedSource runnableClientSource = 
            new OptimisticRunnableClientArrivedSource(rng, governor, 1000, 1600);
        clientSource = runnableClientSource;
        
        Thread producer = new Thread(runnableClientSource);
        producer.start();
        
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
        
        runloop.run(timewarpSources, disp);
        
        runnableClientSource.stop();
        producer.interrupt();
        try {
            producer.join();
        }
        catch (InterruptedException e1) {
        }
    }
}
