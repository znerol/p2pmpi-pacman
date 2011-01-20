package pacifism;

import model.Model;
import model.events.DirectionEvent;
import model.sprites.Sprite;
import p2pmpi.mpi.IntraComm;
import deism.core.Event;
import deism.core.EventCondition;
import deism.p2pmpi.MpiBroadcast;
import deism.p2pmpi.MpiEventGenerator;
import deism.p2pmpi.MpiEventSink;
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

    private final GameGui gui;

    public GameNode(IntraComm mpiCommWorld, int mpiGvtMasterRank,
            int mpiReportTag, int mpiRank, long gvtTimeQuantumSize,
            int pacEventTag, final int pacSpriteId, double timeScale,
            Model model) {
        // Setup environment
        service = new Service();
        governor = new RealtimeExecutionGovernor(timeScale);
        service.register(governor);
        messageCenter = new MessageCenter(governor);
        stateController.setStateObject(service);

        // Setup GVT Client
        final MpiBroadcast gvtMessageFromMaster =
                new MpiBroadcast(mpiCommWorld, mpiGvtMasterRank);
        service.register(gvtMessageFromMaster);
        messageCenter.addEmitter(gvtMessageFromMaster);

        final MpiUnicastEndpoint gvtReportToMaster =
                new MpiUnicastEndpoint(mpiCommWorld, mpiGvtMasterRank,
                        mpiReportTag);
        service.register(gvtReportToMaster);
        messageCenter.addEndpoint(gvtReportToMaster, new ReportMessageFilter());

        Client tqclient =
                new Client(mpiCommWorld.Rank(), gvtTimeQuantumSize,
                        stateController);
        service.register(tqclient);
        messageCenter.addEmitter(tqclient);
        messageCenter.addHandler(tqclient, new GvtMessageFilter());

        // Setup pacman process
        final int MY_RANK = mpiCommWorld.Rank();
        final int PEER_RANK = 1 - MY_RANK;

        DefaultProcessBuilder builder = new DefaultProcessBuilder(service);
        KeyboardController keyboardController =
                new KeyboardController(governor, pacSpriteId);

        // Add own keyboard controller event source
        builder.add(keyboardController);

        // Add event source for peer events
        builder.add(new MpiEventGenerator(mpiCommWorld, PEER_RANK,
                pacEventTag, governor));

        // Send Keyboard Events to other instance
        EventCondition onlyMine = new EventCondition() {
            @Override
            public boolean match(Event event) {
                if (event instanceof DirectionEvent) {
                    DirectionEvent directionEvent = (DirectionEvent) event;
                    return directionEvent.getSprite() == pacSpriteId;
                }
                return false;
            }
        };
        builder.add(new MpiEventSink(mpiCommWorld, PEER_RANK, pacEventTag),
                onlyMine);

        for (Sprite sprite : model.getSprites()) {
            builder.add(sprite);            
        }

        process = builder.getProcess();

        // Setup GUI
        gui = new GameGui(governor, keyboardController, model);
    }

    @Override
    public void run() {
        gui.setVisible(true);
        final Runloop runloop =
                new Runloop(governor, termCond, stateController,
                        snapshotCondition, messageCenter, service);
        runloop.run(process);
    }
}
