package pacifism;

import p2pmpi.mpi.IntraComm;
import p2pmpi.mpi.MPI;
import deism.core.Event;
import deism.core.EventCondition;
import deism.p2pmpi.MpiBroadcast;
import deism.p2pmpi.MpiUnicastEndpoint;
import deism.process.DefaultProcessBuilder;
import deism.process.DiscreteEventProcess;
import deism.run.ExecutionGovernor;
import deism.run.MessageCenter;
import deism.run.RealtimeExecutionGovernor;
import deism.run.Runloop;
import deism.run.Service;
import deism.run.StateController;
import deism.run.StateHistoryController;
import deism.tqgvt.Client;
import deism.tqgvt.GvtMessageFilter;
import deism.tqgvt.ReportMessageFilter;

public class GameNode implements Runnable {
    // Record state history and take a snapshot after every single event
    private final StateController stateController =
            new StateHistoryController();
    private final EventCondition snapshotCondition = new EventCondition() {
        @Override
        public boolean match(Event e) {
            return true;
        }
    };
    private final EventCondition termCond = new EventCondition() {
        @Override
        public boolean match(Event e) {
            return false;
        }
    };

    private final ExecutionGovernor governor;
    private final Service service;
    private final MessageCenter messageCenter;

    private final DiscreteEventProcess process;

    public GameNode(IntraComm mpiCommWorld, int mpiGvtMasterRank,
            int mpiReportTag, int mpiRank, long gvtTimeQuantumSize,
            double timeScale) {
        // Setup environment
        governor = new RealtimeExecutionGovernor(timeScale);
        messageCenter = new MessageCenter(governor);
        service = new Service();
        stateController.setStateObject(service);

        // Setup GVT Client
        final MpiBroadcast gvtMessageFromMaster =
                new MpiBroadcast(mpiCommWorld, mpiGvtMasterRank);
        service.register(gvtMessageFromMaster);
        messageCenter.addEmitter(gvtMessageFromMaster);

        final MpiUnicastEndpoint gvtReportToMaster =
                new MpiUnicastEndpoint(MPI.COMM_WORLD, mpiGvtMasterRank,
                        mpiReportTag);
        service.register(gvtReportToMaster);
        messageCenter.addEndpoint(gvtReportToMaster, new ReportMessageFilter());

        Client tqclient =
                new Client(MPI.COMM_WORLD.Rank(), gvtTimeQuantumSize,
                        stateController);
        service.register(tqclient);
        messageCenter.addEmitter(tqclient);
        messageCenter.addHandler(tqclient, new GvtMessageFilter());

        // Setup pacman process
        DefaultProcessBuilder builder = new DefaultProcessBuilder(service);

        // FIXME: Add pacman process here
        process = builder.getProcess();
    }

    @Override
    public void run() {
        final Runloop runloop =
                new Runloop(governor, termCond, stateController,
                        snapshotCondition, messageCenter, service);
        runloop.run(process);
    }
}
