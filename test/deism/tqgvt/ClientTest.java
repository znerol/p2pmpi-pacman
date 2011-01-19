package deism.tqgvt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import deism.core.Event;
import deism.ipc.base.Endpoint;
import deism.ipc.base.Message;
import deism.run.StateController;
import deism.run.SystemTimeProxy;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientTest {
    @Mock
    private SystemTimeProxy systime;
    @Mock
    private Endpoint<Message> master;
    @Mock
    private StateController stateController;
    @InjectMocks
    private Client client = new Client(1, 100, null);

    /**
     * Verify that the time quantum calculation works with tqlength = 100
     */
    @Test
    public void testGetCurrentTq() {
        when(systime.get()).thenReturn(0L);
        client.start(0);

        long result;
        when(systime.get()).thenReturn(1L);
        result = client.getCurrentTq();
        assertEquals(0L, result);

        when(systime.get()).thenReturn(99L);
        result = client.getCurrentTq();
        assertEquals(0L, result);

        when(systime.get()).thenReturn(100L);
        result = client.getCurrentTq();
        assertEquals(1L, result);

        when(systime.get()).thenReturn(101L);
        result = client.getCurrentTq();
        assertEquals(1L, result);

        when(systime.get()).thenReturn(200L);
        result = client.getCurrentTq();
        assertEquals(2L, result);

        verifyNoMoreInteractions(master);
        verifyNoMoreInteractions(stateController);
    }

    /**
     * Verify that a report message is generated containing the process id, the
     * last time quantum and the lvt when tq value changes.
     */
    @Test
    public void testUpdateReportNoMessagesInTransit() {
        when(systime.get()).thenReturn(1010L); // -> tq = 10
        client.start(0);

        when(systime.get()).thenReturn(1122L); // -> tq = 11
        client.update(42); // update lvt
        client.updateReport(); // generate new report message

        ArgumentCaptor<ReportMessage> arg =
                ArgumentCaptor.forClass(ReportMessage.class);
        verify(master).send(arg.capture());
        ReportMessage result = arg.getValue();
        assertEquals(1, result.getProcess());
        assertEquals(10, result.getTq());
        assertEquals(42, result.getLvt());

        // ensure that message counters are 0
        assertEquals(Long.MAX_VALUE, result.getMvt());
        assertEquals(0, result.getSend());
        assertTrue(result.getRecv().size() == 0);

        verifyNoMoreInteractions(master);
        verifyNoMoreInteractions(stateController);
    }

    /**
     * Test that send counter and mvt value get updated correctly when events
     * are sent over the wire
     */
    @Test
    public void testEventExporter() {
        final Event event = new Event(1);

        when(systime.get()).thenReturn(1010L); // -> tq = 10
        client.start(0);

        Event resultEvent = client.pack(event);

        // Verify that the correct time quantum is appended to the wrapped event
        assertThat(resultEvent, instanceOf(WrappedEvent.class));
        WrappedEvent wrappedEvent = (WrappedEvent) resultEvent;
        assertEquals(10, wrappedEvent.getTq());
        assertEquals(event, wrappedEvent.getEvent());

        when(systime.get()).thenReturn(1122L); // -> tq = 11
        client.updateReport(); // generate new report message

        ArgumentCaptor<ReportMessage> arg =
                ArgumentCaptor.forClass(ReportMessage.class);
        verify(master).send(arg.capture());
        ReportMessage result = arg.getValue();

        // Verify that the mvt value equals the events simtime and that the
        // send-counter was incremented.
        assertEquals(1, result.getMvt());
        assertEquals(1, result.getSend());
        assertTrue(result.getRecv().size() == 0);

        verifyNoMoreInteractions(master);
        verifyNoMoreInteractions(stateController);
    }

    /**
     * Test that recv counter is reported correctly when events are received
     * from other processes.
     */
    @Test
    public void testEventImporter() {
        final long sourceTq = 5;
        final Event originalEvent = new Event(1);
        final WrappedEvent wrappedEvent =
                new WrappedEvent(originalEvent, sourceTq);

        when(systime.get()).thenReturn(1010L); // -> tq = 10
        client.start(0);

        Event resultEvent = client.unpack(wrappedEvent);

        // Verify that original event was unpacked correctly
        assertEquals(resultEvent, originalEvent);

        when(systime.get()).thenReturn(1122L); // -> tq = 11
        client.updateReport(); // generate new report message

        ArgumentCaptor<ReportMessage> arg =
                ArgumentCaptor.forClass(ReportMessage.class);
        verify(master).send(arg.capture());
        ReportMessage result = arg.getValue();

        // Verify that the mvt and send counter are not affected from receive
        // operation
        assertEquals(Long.MAX_VALUE, result.getMvt());
        assertEquals(0, result.getSend());

        // verify that the recv table now contains an entry 5 => 1 (one event
        // received from tq=5)
        assertEquals(result.getRecv().size(), 1);
        assertEquals((long)result.getRecv().get(5L), 1L);

        verifyNoMoreInteractions(master);
        verifyNoMoreInteractions(stateController);
    }

    /**
     * Verify that the gvt client commits object state when a gvt message is
     * received from the master.
     */
    @Test
    public void testGvtMessageHandler() {
        GvtMessage gvtMessage = new GvtMessage(42);
        client.handle(gvtMessage);
        verify(stateController).commit(42L);

        verifyNoMoreInteractions(master);
        verifyNoMoreInteractions(stateController);
    }
}
