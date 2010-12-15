package wq1;

import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.BasicConfigurator;

import util.TerminateAfterDuration;
import wqcommon.ClientArrivedEvent;
import wqcommon.OptimisticRunnableClientArrivedSource;

import deism.core.Event;
import deism.core.EventCondition;
import deism.run.EventRunloop;
import deism.run.EventRunloopRecoveryStrategy;
import deism.run.ExecutionGovernor;
import deism.run.FastForwardRunloop;
import deism.run.RealtimeExecutionGovernor;
import deism.run.TimewarpRunloopRecoveryStrategy;
import deism.stateful.DefaultTimewarpDiscreteEventProcess;
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

        DefaultTimewarpDiscreteEventProcess process = 
            new DefaultTimewarpDiscreteEventProcess();

        OptimisticRunnableClientArrivedSource clientArrivedSource =
            new OptimisticRunnableClientArrivedSource(rng, governor, speed,
                    1000, 1600);
        process.addStartable(clientArrivedSource);
        process.addEventSource(
                new TimewarpEventSourceAdapter(clientArrivedSource));
        
        PriorityBlockingQueue<ClientArrivedEvent> jobs =
            new PriorityBlockingQueue<ClientArrivedEvent>();
        process.addEventSource(
                new TimewarpEventSourceAdapter(new ClerkSource(jobs)));
        process.addEventSource(
                new TimewarpEventSourceAdapter(new ClerkSource(jobs)));

        process.addEventDispatcher(new JobAggregator(jobs));
        
        EventCondition snapshotAll = new EventCondition() {
            @Override
            public boolean match(Event e) {
                return true;
            }
        };

        EventRunloopRecoveryStrategy recoveryStrategy =
            new TimewarpRunloopRecoveryStrategy(process);

        EventRunloop runloop = new FastForwardRunloop(governor, termCond,
                recoveryStrategy, snapshotAll);

        runloop.run(process);
    }
}
