package deism.run;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import deism.ipc.base.Condition;
import deism.ipc.base.Emitter;
import deism.ipc.base.Endpoint;
import deism.ipc.base.Handler;
import deism.ipc.base.Message;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MessageCenterTest {
    @Mock
    private Message message;
    @Mock
    private Emitter<Message> emitter;
    @Mock
    private Endpoint<Message> endpoint;
    @Mock
    private Condition<Message> endpointFilter;
    @Mock
    private Handler<Message> handler;
    @Mock
    private Condition<Message> handlerFilter;
    @Mock
    private ExecutionGovernor governor;

    private MessageCenter messageCenter;

    @Before
    public void setUp() {
        messageCenter = new MessageCenter(governor);
    }

    /**
     * The message center neither has a message queued nor a handler/endpoint
     * configured. No interactien with the mocks expected.
     */
    @Test
    public void idleProcess() {
        messageCenter.process();

        verifyNoMoreInteractions(governor);
    }

    /**
     * Queue a message and verify that the governor is resumed.
     */
    @Test
    public void processNoHandler() {
        messageCenter.send(message);
        messageCenter.process();

        verify(governor).resume();

        verifyNoMoreInteractions(message);
        verifyNoMoreInteractions(governor);
    }

    /**
     * Ensure that a message is delivered to the registered handler and endpoint
     */
    @Test
    public void processOneMessageWithHandlerAndEndpoint() {
        messageCenter.addEndpoint(endpoint);
        messageCenter.addHandler(handler);

        messageCenter.send(message);
        messageCenter.process();

        verify(endpoint).send(message);
        verify(handler).handle(message);

        verifyNoMoreInteractions(message);
        verifyNoMoreInteractions(endpoint);
        verifyNoMoreInteractions(handler);
    }

    /**
     * Ensure that process() respects endpoint and handler filters
     */
    @Test
    public void processOneMessageWithFilter() {
        // endpoint filter matches first, handler filter second message
        when(endpointFilter.match(any(Message.class))).thenReturn(true, false);
        when(handlerFilter.match(any(Message.class))).thenReturn(false, true);

        messageCenter.addEndpoint(endpoint, endpointFilter);
        messageCenter.addHandler(handler, handlerFilter);

        messageCenter.send(message);
        messageCenter.send(message);
        messageCenter.process();

        verify(endpoint).send(message);
        verify(handler).handle(message);

        verify(endpointFilter, times(2)).match(message);
        verify(handlerFilter, times(2)).match(message);

        verifyNoMoreInteractions(message);
        verifyNoMoreInteractions(endpoint);
        verifyNoMoreInteractions(endpointFilter);
        verifyNoMoreInteractions(handler);
        verifyNoMoreInteractions(handlerFilter);
    }

    /**
     * Verify that addEmitter just calls setEndpoint on the given emitter
     */
    @Test
    public void testAddEmitter() {
        messageCenter.addEmitter(emitter);
        verify(emitter).setEndpoint(messageCenter);

        verifyNoMoreInteractions(emitter);
    }
}
