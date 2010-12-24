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
import deism.process.DefaultDiscreteEventProcess;
import deism.process.DefaultProcessBuilder;
import deism.run.MessageCenter;
import deism.run.Service;
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

        Service service = new Service();
        
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

        service.addStartable(governor);

        DefaultDiscreteEventProcess process = new DefaultDiscreteEventProcess();
        DefaultProcessBuilder builder =
                new DefaultProcessBuilder(process, service);

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

        MessageCenter messageCenter = new MessageCenter(governor);

        Runloop runloop = new Runloop(governor, termCond, stateController,
                noSnapshots, messageCenter, service);
        runloop.run(process);
    }
}
