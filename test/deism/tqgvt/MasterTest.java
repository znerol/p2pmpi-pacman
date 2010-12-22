package deism.tqgvt;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import deism.ipc.base.MessageSender;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MasterTest {
    @Mock
    private MessageSender clients;

    private Master master;

    @Before
    public void setUp() {
        master = new Master(2, clients);
    }

    @Test
    public void testIncompleteInformation() {
        // given is a master with two clients, client one sends periodic
        // reports, client two does not send any report.

        // p=0 tq=1 lvt=5 mvt=max send=0 recv=0
        master.handle(new ReportMessage(0, 1, 5, Long.MAX_VALUE, 0,
                new HashMap<Long, Long>()));
        // p=0 tq=2 lvt=140 mvt=250 send=1 recv=0
        master.handle(new ReportMessage(0, 2, 140, 250, 1,
                new HashMap<Long, Long>()));
        // p=0 tq=3 lvt=244 mvt=max send=0 recv=0
        master.handle(new ReportMessage(0, 3, 244, Long.MAX_VALUE, 0,
                new HashMap<Long, Long>()));

        verifyNoMoreInteractions(clients);
    }

    /**
     * Given is a master with two clients (tqlength = 100).
     * 
     * When both clients run in parallel without exchanging any messages
     * 
     * Then the gvt must follow the smallest lvt.
     */
    @Test
    public void testFollowLvt() {

        // p=0 tq=1 lvt=99 mvt=max send=0 recv=0
        master.handle(new ReportMessage(0, 1, 99, Long.MAX_VALUE, 0,
                new HashMap<Long, Long>()));
        // p=1 tq=1 lvt=50 mvt=max send=0 recv=0
        master.handle(new ReportMessage(1, 1, 50, Long.MAX_VALUE, 0,
                new HashMap<Long, Long>()));

        // p=0 tq=2 lvt=120 mvt=max send=0 recv=0
        master.handle(new ReportMessage(0, 2, 120, Long.MAX_VALUE, 0,
                new HashMap<Long, Long>()));
        // p=1 tq=1 lvt=88 mvt=max send=0 recv=0
        master.handle(new ReportMessage(1, 1, 88, Long.MAX_VALUE, 0,
                new HashMap<Long, Long>()));

        // p=0 tq=3 lvt=201 mvt=max send=0 recv=0
        master.handle(new ReportMessage(0, 3, 201, Long.MAX_VALUE, 0,
                new HashMap<Long, Long>()));
        // p=1 tq=2 lvt=127 mvt=max send=0 recv=0
        master.handle(new ReportMessage(1, 2, 127, Long.MAX_VALUE, 0,
                new HashMap<Long, Long>()));

        verify(clients).send(new GvtMessage(50));
        verify(clients).send(new GvtMessage(88));
        verify(clients).send(new GvtMessage(127));
        verifyNoMoreInteractions(clients);
    }

    /**
     * Given is a master with two clients (tqlength = 100).
     * 
     * When both clients run in parallel and p0 sends p1 a message for
     * simulation time 87 which does not arrive within the observed timeframe
     * 
     * Then the gvt must follow the smallest lvt until mvt=87 of p0 at tq=1
     */
    @Test
    public void testFollowLvtUpToMvt() {

        // p=0 tq=1 lvt=99 mvt=max send=0 recv=0
        master.handle(new ReportMessage(0, 1, 99, 87, 1,
                new HashMap<Long, Long>()));
        // p=1 tq=1 lvt=50 mvt=max send=0 recv=0
        master.handle(new ReportMessage(1, 1, 50, Long.MAX_VALUE, 0,
                new HashMap<Long, Long>()));

        // p=0 tq=2 lvt=120 mvt=128 send=0 recv=0
        master.handle(new ReportMessage(0, 2, 120, Long.MAX_VALUE, 0,
                new HashMap<Long, Long>()));
        // p=1 tq=1 lvt=88 mvt=max send=0 recv=0
        master.handle(new ReportMessage(1, 1, 88, Long.MAX_VALUE, 0,
                new HashMap<Long, Long>()));

        // p=0 tq=3 lvt=201 mvt=max send=0 recv=0
        master.handle(new ReportMessage(0, 3, 201, Long.MAX_VALUE, 0,
                new HashMap<Long, Long>()));
        // p=1 tq=2 lvt=127 mvt=max send=0 recv=0
        master.handle(new ReportMessage(1, 2, 127, Long.MAX_VALUE, 0,
                new HashMap<Long, Long>()));

        verify(clients).send(new GvtMessage(50));
        verify(clients).send(new GvtMessage(87));
        verifyNoMoreInteractions(clients);
    }

    /**
     * Given is a master with two clients (tqlength = 100).
     * 
     * When both clients run in parallel and p0 sends p1 a message for
     * simulation time 87 which arrives at p1 in tq=1
     * 
     * Then the gvt must follow the smallest lvt until mvt=87 and must continue
     * following lvt as soon as p1 reported the receipton of that message.
     */
    @Test
    public void testFollowLvtUpToMvtContinueAfterReceive() {

        // p=0 tq=1 lvt=99 mvt=max send=0 recv=0
        master.handle(new ReportMessage(0, 1, 99, 87, 1,
                new HashMap<Long, Long>()));
        // p=1 tq=1 lvt=50 mvt=max send=0 recv=0
        master.handle(new ReportMessage(1, 1, 50, Long.MAX_VALUE, 0,
                new HashMap<Long, Long>()));

        // p=0 tq=2 lvt=120 mvt=128 send=0 recv=0
        master.handle(new ReportMessage(0, 2, 120, Long.MAX_VALUE, 0,
                new HashMap<Long, Long>()));
        // p=1 tq=1 lvt=88 mvt=max send=0 recv=0
        HashMap<Long, Long> recv = new HashMap<Long, Long>();
        recv.put(1L, 1L);
        master.handle(new ReportMessage(1, 1, 88, Long.MAX_VALUE, 0, recv));

        // p=0 tq=3 lvt=201 mvt=max send=0 recv=0
        master.handle(new ReportMessage(0, 3, 201, Long.MAX_VALUE, 0,
                new HashMap<Long, Long>()));
        // p=1 tq=2 lvt=127 mvt=max send=0 recv=0
        master.handle(new ReportMessage(1, 2, 127, Long.MAX_VALUE, 0,
                new HashMap<Long, Long>()));

        verify(clients).send(new GvtMessage(50));
        verify(clients).send(new GvtMessage(88));
        verify(clients).send(new GvtMessage(127));
        verifyNoMoreInteractions(clients);
    }
}
