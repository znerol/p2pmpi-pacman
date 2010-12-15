package wq1;

import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.BasicConfigurator;

import util.TerminateAfterDuration;
import wqcommon.ClientArrivedEvent;
import wqcommon.ClientArrivedGenerator;
import wqcommon.PestimisticRunnableClientArrivedSource;

import deism.adapter.EventSourceStatefulGeneratorAdapter;
import deism.core.Event;
import deism.core.EventCondition;
import deism.run.DefaultDiscreteEventProcess;
import deism.run.EventRunloopRecoveryStrategy;
import deism.run.ExecutionGovernor;
import deism.run.FailFastRunloopRecoveryStrategy;
import deism.run.DefaultEventRunloop;
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

        DefaultDiscreteEventProcess process = new DefaultDiscreteEventProcess();

        boolean multithread = Boolean.getBoolean("simulationMultithread");
        if (multithread) {
            process.addEventSource(new PestimisticRunnableClientArrivedSource(
                    rng, governor, 1000, 1600));
        }
        else {
            process.addEventSource(new EventSourceStatefulGeneratorAdapter(
                new ClientArrivedGenerator(rng, 1000, 1600)));
        }
        
        PriorityBlockingQueue<ClientArrivedEvent> jobs =
            new PriorityBlockingQueue<ClientArrivedEvent>();
        process.addEventSource(new ClerkSource(jobs));
        process.addEventSource(new ClerkSource(jobs));

        process.addEventDispatcher(new JobAggregator(jobs));
        
        EventRunloopRecoveryStrategy recoveryStrategy =
            new FailFastRunloopRecoveryStrategy();

        EventCondition noSnapshots = new EventCondition() {
            @Override
            public boolean match(Event e) {
                return false;
            }
        };
        
        DefaultEventRunloop runloop = new DefaultEventRunloop(governor, termCond,
                recoveryStrategy, noSnapshots);
        runloop.run(process);
    }
}
