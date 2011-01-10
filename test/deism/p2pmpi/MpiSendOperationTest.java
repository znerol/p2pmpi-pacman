package deism.p2pmpi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import deism.core.Event;

import p2pmpi.mpi.IntraComm;
import p2pmpi.mpi.MPI;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MpiSendOperationTest {
    @Mock
    private IntraComm comm;
    private MpiSendOperation<Event> operation;

    @Before
    public void setUp() {
        operation = new MpiSendOperation<Event>(comm, 1, 2);
    }

    @Test
    public void testMpiSend() {
        Event event = new Event(1);
        Event[] eventBuffer = {event};

        operation.send(event);
        verify(comm).Send(eventBuffer, 0, 1, MPI.OBJECT, 1, 2);
        verifyNoMoreInteractions(comm);
    }
}
