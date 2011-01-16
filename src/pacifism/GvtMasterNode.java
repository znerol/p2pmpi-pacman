package pacifism;

import p2pmpi.mpi.IntraComm;
import p2pmpi.mpi.MPI;
import deism.core.Event;
import deism.core.EventCondition;
import deism.p2pmpi.MpiBroadcast;
import deism.p2pmpi.MpiUnicastListener;
import deism.process.DefaultDiscreteEventProcess;
import deism.process.DiscreteEventProcess;
import deism.run.ExecutionGovernor;
import deism.run.MessageCenter;
import deism.run.NoStateController;
import deism.run.RealtimeExecutionGovernor;
import deism.run.Runloop;
import deism.run.Service;
import deism.run.StateController;
import deism.tqgvt.GvtMessageFilter;
import deism.tqgvt.Master;
import deism.tqgvt.ReportMessageFilter;

public class GvtMasterNode {
    // We don't actually do any simulation on this node, therefore neither
    // state controller nor snapshot condition is necessary here
    private final StateController stateController = new NoStateController();
    private final EventCondition snapshotCondition = new EventCondition() {
        @Override
        public boolean match(Event e) {
            return false;
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

    // dummy process
    private final DiscreteEventProcess process =
            new DefaultDiscreteEventProcess();

    public GvtMasterNode(IntraComm mpiCommWorld, int mpiGvtMasterRank,
            int mpiGvtReportTag, int gvtClientCount, double timeScale) {
        // Setup environment
        governor = new RealtimeExecutionGovernor(timeScale);
        messageCenter = new MessageCenter(governor);
        service = new Service();
        stateController.setStateObject(service);

        // Setup GVT Master
        final MpiBroadcast gvtMessageToClients =
                new MpiBroadcast(mpiCommWorld, mpiGvtMasterRank);
        service.register(gvtMessageToClients);
        messageCenter.addEndpoint(gvtMessageToClients, new GvtMessageFilter());

        final MpiUnicastListener gvtReportFromClients =
                new MpiUnicastListener(mpiCommWorld, MPI.ANY_SOURCE,
                        mpiGvtReportTag);
        service.register(gvtReportFromClients);
        messageCenter.addEmitter(gvtReportFromClients);

        final Master tqmaster = new Master(gvtClientCount);
        service.register(tqmaster);
        messageCenter.addEmitter(tqmaster);
        messageCenter.addHandler(tqmaster, new ReportMessageFilter());
    }

    public void run() {
        final Runloop runloop =
                new Runloop(governor, termCond, stateController,
                        snapshotCondition, messageCenter, service);
        runloop.run(process);
    }
}
