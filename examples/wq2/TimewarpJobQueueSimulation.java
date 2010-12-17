package wq2;

import java.util.Random;

import org.apache.log4j.BasicConfigurator;

import util.JitterEventSource;
import util.TerminateAfterDuration;
import wqcommon.ClientArrivedGenerator;

import deism.core.Event;
import deism.core.EventCondition;
import deism.run.EventRunloop;
import deism.run.EventRunloopRecoveryStrategy;
import deism.run.ExecutionGovernor;
import deism.run.DefaultEventRunloop;
import deism.run.ImmediateExecutionGovernor;
import deism.run.RealtimeExecutionGovernor;
import deism.run.TimewarpRunloopRecoveryStrategy;
import deism.stateful.DefaultTimewarpDiscreteEventProcess;
import deism.stateful.DefaultTimewarpProcessBuilder;

public class TimewarpJobQueueSimulation {
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
        if (speed > 0) {
            /* run simulation in realtime */
            governor = new RealtimeExecutionGovernor(speed);
        }
        else {
            /* run simulation as fast as possible */
            governor = new ImmediateExecutionGovernor();
        }

        DefaultTimewarpDiscreteEventProcess process = 
            new DefaultTimewarpDiscreteEventProcess();
        DefaultTimewarpProcessBuilder builder = 
            new DefaultTimewarpProcessBuilder(process, governor);

        builder.add(new ClientArrivedGenerator(rng, 1000, 1600));
        
        WaitingRoom waitingRoom = new WaitingRoom();
        builder.add(waitingRoom.source);
        builder.add(waitingRoom.dispatcher);

        Counter counterOne = new Counter();
        builder.add(counterOne);

        Counter counterTwo = new Counter();
        builder.add(counterTwo);

        // add jitter
        builder.add(new JitterEventSource());

        // queue logger
        builder.add(waitingRoom.statisticsLogger);
        
        EventCondition snapshotAll = new EventCondition() {
            @Override
            public boolean match(Event e) {
                return true;
            }
        };

        EventRunloopRecoveryStrategy recoveryStrategy =
            new TimewarpRunloopRecoveryStrategy(process);

        EventRunloop runloop = new DefaultEventRunloop(governor, termCond,
                recoveryStrategy, snapshotAll);

        runloop.run(process);
    }
}
