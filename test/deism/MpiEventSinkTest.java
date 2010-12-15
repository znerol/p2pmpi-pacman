package deism;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import deism.core.Event;
import deism.p2pmpi.MpiEventSink;

import p2pmpi.mpi.IntraComm;
import p2pmpi.mpi.MPI;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MpiEventSinkTest {
    @Mock
    private IntraComm comm;
    private MpiEventSink sink;

    @Before
    public void setUp() {
        sink = new MpiEventSink(comm, 1, 2);
    }

    @Test
    public void testMpiSend() {
        Event event = new Event(1);
        Event[] eventBuffer = {event};

        sink.offer(event);
        verify(comm).Send(eventBuffer, 0, 1, MPI.OBJECT, 1, 2);
        verifyNoMoreInteractions(comm);
    }
}
