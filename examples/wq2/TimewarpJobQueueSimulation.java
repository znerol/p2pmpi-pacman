package wq2;

import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.BasicConfigurator;

import util.EventLogger;
import util.JitterEventSource;
import util.StateHistoryLogger;
import util.TerminateAfterDuration;
import wqcommon.ClientArrivedSource;

import deism.Event;
import deism.EventDispatcher;
import deism.EventCondition;
import deism.EventDispatcherCollection;
import deism.EventRunloop;
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
import deism.StateHistory;
import deism.TimewarpEventSource;
import deism.TimewarpRunloopRecoveryStrategy;
import deism.TimewarpEventSourceAdapter;

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
