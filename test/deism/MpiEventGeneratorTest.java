package deism;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import deism.core.Event;
import deism.p2pmpi.MpiEventGenerator;
import deism.run.ExecutionGovernor;

import p2pmpi.mpi.IntraComm;
import p2pmpi.mpi.MPI;
import p2pmpi.mpi.Status;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MpiEventGeneratorTest {

    @Mock
    private IntraComm comm;
    @Mock
    private ExecutionGovernor governor;

    private MpiEventGenerator generator;

    @Before
    public void setUp() {
        generator = new MpiEventGenerator(comm, 1, 2, governor);
    }

    @Test
    public void testMpiReceive() {
        final Event event = new Event(1);
        final Event[] eventBuffer = { null };

        when(comm.Recv(eventBuffer, 0, 1, MPI.OBJECT, 1, 2)).thenAnswer(
                new Answer<Status>() {
                    @Override
                    public Status answer(InvocationOnMock invocation)
                            throws Throwable {
                        // place event into the buffer array (first argument)
                        Event[] buffer = (Event[])invocation.getArguments()[0];
                        buffer[0] = event;
                        return new Status(1, 2, 1);
                    }
                });

        Event result = generator.poll();
        assertEquals(event, result);
    }
}
