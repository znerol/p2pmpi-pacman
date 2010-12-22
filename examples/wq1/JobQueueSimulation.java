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
import deism.core.EventExporter;
import deism.core.EventImporter;
import deism.ipc.base.Message;
import deism.ipc.base.MessageHandler;
import deism.ipc.base.MessageQueue;
import deism.process.DefaultDiscreteEventProcess;
import deism.process.DefaultProcessBuilder;
import deism.run.DefaultRunloopMessageQueue;
import deism.run.StateController;
import deism.run.ExecutionGovernor;
import deism.run.NoStateController;
import deism.run.Runloop;
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

        DefaultDiscreteEventProcess process = new DefaultDiscreteEventProcess();
        DefaultProcessBuilder builder = new DefaultProcessBuilder(process,
                fakeImporter, fakeExporter);

        boolean multithread = Boolean.getBoolean("simulationMultithread");
        if (multithread) {
            builder.add(new PestimisticRunnableClientArrivedSource(
                    rng, governor, 1000, 1600));
        }
        else {
            builder.add(new EventSourceStatefulGeneratorAdapter(
                new ClientArrivedGenerator(rng, 1000, 1600)));
        }

        PriorityBlockingQueue<ClientArrivedEvent> jobs =
            new PriorityBlockingQueue<ClientArrivedEvent>();
        builder.add(new ClerkSource(jobs));
        builder.add(new ClerkSource(jobs));

        builder.add(new JobAggregator(jobs));
        
        StateController stateController =
            new NoStateController();

        EventCondition noSnapshots = new EventCondition() {
            @Override
            public boolean match(Event e) {
                return false;
            }
        };

        MessageHandler messageHandler = new MessageHandler() {
            @Override
            public void handle(Message item) {
            }
        };

        MessageQueue messageQueue = new DefaultRunloopMessageQueue(governor);

        Runloop runloop = new Runloop(governor, termCond,
                stateController, noSnapshots, messageQueue, messageHandler);
        runloop.run(process);
    }
}
