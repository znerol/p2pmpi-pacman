package wq1;

import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.BasicConfigurator;

import util.TerminateAfterDuration;
import wqcommon.ClientArrivedEvent;
import wqcommon.OptimisticRunnableClientArrivedSource;

import deism.core.Event;
import deism.core.EventCondition;
import deism.ipc.base.EventExporter;
import deism.ipc.base.EventImporter;
import deism.process.DefaultDiscreteEventProcess;
import deism.process.DefaultProcessBuilder;
import deism.run.MessageCenter;
import deism.run.ExecutionGovernor;
import deism.run.LvtListener;
import deism.run.Runloop;
import deism.run.RealtimeExecutionGovernor;
import deism.run.Service;
import deism.run.StateHistoryController;

public class StupidTimewarpJobQueueSimulation {
    /**
     * @param args
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        Random rng = new Random(1234);

        /* exit simulation after n units of simulation time */
        EventCondition termCond = new TerminateAfterDuration(1000 * 50);

        Service service = new Service();

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

        service.addStartable(governor);

        StateHistoryController stateController = new StateHistoryController();
        stateController.setStateObject(service);

        DefaultDiscreteEventProcess process = new DefaultDiscreteEventProcess();
        DefaultProcessBuilder builder =
                new DefaultProcessBuilder(process, fakeImporter, fakeExporter,
                        service);

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

        LvtListener lvtListener = new LvtListener() {
            @Override
            public void update(long lvt) {
            }
        };

        MessageCenter messageCenter = new MessageCenter(governor);

        Runloop runloop =
                new Runloop(governor, termCond, stateController, snapshotAll,
                        messageCenter, lvtListener, service);

        runloop.run(process);
    }
}
