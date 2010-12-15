package wq2;

import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.BasicConfigurator;

import util.EventLogger;
import util.JitterEventSource;
import util.StateHistoryLogger;
import util.TerminateAfterDuration;
import wqcommon.ClientArrivedSource;

import deism.core.Event;
import deism.core.EventCondition;
import deism.core.EventDispatcher;
import deism.core.EventDispatcherCollection;
import deism.core.EventSink;
import deism.core.EventSinkCollection;
import deism.core.EventSource;
import deism.core.EventSourceCollection;
import deism.run.EventRunloop;
import deism.run.EventRunloopRecoveryStrategy;
import deism.run.ExecutionGovernor;
import deism.run.FailFastRunloopRecoveryStrategy;
import deism.run.FastForwardRunloop;
import deism.run.ImmediateExecutionGovernor;
import deism.run.RealtimeExecutionGovernor;
import deism.run.TimewarpRunloopRecoveryStrategy;
import deism.stateful.StateHistory;
import deism.stateful.TimewarpEventSource;
import deism.stateful.TimewarpEventSourceAdapter;

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
//        governor = new RealtimeExecutionGovernor(speed);
        governor = new ImmediateExecutionGovernor();

//        RunnableClientArrivedSource<Long> runnableClientSource = 
//            new RunnableClientArrivedSource<Long>(rng, governor, 1000, 1600);
//        Thread producer = new Thread(runnableClientSource);
//        producer.start();
//        TimewarpEventSource timewarpClientSource =
//            new TimewarpEventSourceAdapter(runnableClientSource);
        
        EventSource clientSource = new ClientArrivedSource(rng, 1000, 1600);
        TimewarpEventSource timewarpClientSource =
            new TimewarpEventSourceAdapter(clientSource);
        
        WaitingRoom waitingRoom = new WaitingRoom();
        Counter counterOne = new Counter();
        Counter counterTwo = new Counter();
        
        TimewarpEventSource jitterSource = new TimewarpEventSourceAdapter(
                new JitterEventSource());

        EventSource[] sources = {
                timewarpClientSource,
                waitingRoom.source,
                counterOne,
                counterTwo,
                jitterSource,
        };
        
        EventDispatcher eventLogger = new EventLogger();
        EventDispatcher[] dispatchers = {
                waitingRoom.dispatcher,
                counterOne,
                counterTwo,
                eventLogger,
                waitingRoom.statisticsLogger,
        };
        
        EventCondition snapshotAll = new EventCondition() {
            @Override
            public boolean match(Event e) {
                return true;
            }
        };
        
        ArrayList<StateHistory<Long>> stateObjects =
            new ArrayList<StateHistory<Long>>();
        stateObjects.add(new StateHistoryLogger());
        stateObjects.add(timewarpClientSource);
        stateObjects.add(waitingRoom.source);
        stateObjects.add(waitingRoom.dispatcher);
        stateObjects.add(counterOne);
        stateObjects.add(counterTwo);
        stateObjects.add(jitterSource);

        EventRunloopRecoveryStrategy recoveryStrategy =
            new TimewarpRunloopRecoveryStrategy(stateObjects);

        EventRunloop runloop = new FastForwardRunloop(governor, termCond,
                recoveryStrategy, snapshotAll);

        EventSource eventSource = new EventSourceCollection(sources);
        EventDispatcher eventDispatcher =
            new EventDispatcherCollection(dispatchers);

        runloop.run(eventSource,
                new EventSinkCollection(new ArrayList<EventSink>()),
                eventDispatcher);

//        runnableClientSource.stop();
//        producer.interrupt();
//        try {
//            producer.join();
//        }
//        catch (InterruptedException e1) {
//        }
    }
}
