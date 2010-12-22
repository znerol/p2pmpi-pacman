package wq1;

import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.BasicConfigurator;

import util.TerminateAfterDuration;
import wqcommon.ClientArrivedEvent;
import wqcommon.OptimisticRunnableClientArrivedSource;

import deism.core.Event;
import deism.core.EventCondition;
import deism.core.EventExporter;
import deism.core.EventImporter;
import deism.ipc.base.Message;
import deism.ipc.base.MessageHandler;
import deism.run.EventRunloop;
import deism.run.StateController;
import deism.run.ExecutionGovernor;
import deism.run.DefaultEventRunloop;
import deism.run.RealtimeExecutionGovernor;
import deism.run.StateHistoryController;
import deism.stateful.DefaultTimewarpDiscreteEventProcess;
import deism.stateful.DefaultTimewarpProcessBuilder;

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

        EventImporter fakeImporter = new EventImporter() {
            @Override
            public Event unpack(Event event) {
                return event;
            }
        };

        EventExporter fakeExporter = new EventExporter() {
            @Override
            public Event pack(Event event) {
                return event;
            }
        };

        DefaultTimewarpDiscreteEventProcess process = 
            new DefaultTimewarpDiscreteEventProcess();
        DefaultTimewarpProcessBuilder builder = new DefaultTimewarpProcessBuilder(
                process, fakeImporter, fakeExporter);

        OptimisticRunnableClientArrivedSource clientArrivedSource =
            new OptimisticRunnableClientArrivedSource(rng, governor, speed,
                    1000, 1600);

        builder.add(clientArrivedSource);

        PriorityBlockingQueue<ClientArrivedEvent> jobs =
            new PriorityBlockingQueue<ClientArrivedEvent>();
        builder.add(new ClerkSource(jobs));
        builder.add(new ClerkSource(jobs));
        builder.add(new JobAggregator(jobs));

        EventCondition snapshotAll = new EventCondition() {
            @Override
            public boolean match(Event e) {
                return true;
            }
        };

        StateController stateController =
            new StateHistoryController(process);

        MessageHandler messageHandler = new MessageHandler() {
            @Override
            public void handle(Message item) {
            }
        };

        EventRunloop runloop = new DefaultEventRunloop(governor, termCond,
                stateController, snapshotAll, messageHandler);

        runloop.run(process);
    }
}
