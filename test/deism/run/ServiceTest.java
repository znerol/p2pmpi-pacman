package deism.run;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import deism.core.Event;
import deism.core.Flushable;
import deism.core.Startable;
import deism.ipc.base.EventExporter;
import deism.ipc.base.EventImporter;
import deism.stateful.StateHistory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServiceTest {
    @Mock
    private Startable startable;
    @Mock
    private Flushable flushable;
    @Mock
    private StateHistory<Long> stateHistoryObject;
    @Mock
    private EventImporter eventImporter;
    @Mock
    private EventExporter eventExporter;
    @Mock
    private LvtListener lvtListener;
    
    private Service service = new Service();
    
    /**
     * Verify that when an importer/exporter is registered with the service,
     * pack and unpack will proxy the request to those.
     */
    @Test
    public void testImporterExporter() {
        Event original = new Event(0);
        Event wrapped = new Event(1);

        when(eventExporter.pack(original)).thenReturn(wrapped);
        when(eventImporter.unpack(wrapped)).thenReturn(original);

        service.register(eventExporter);
        service.register(eventImporter);
        
        Event result;
        result = service.pack(original);
        assertEquals(result, wrapped);
        result = service.unpack(wrapped);
        assertEquals(result, original);

        verify(eventExporter).pack(original);
        verify(eventImporter).unpack(wrapped);
        verifyNoMoreInteractions(startable);
        verifyNoMoreInteractions(flushable);
        verifyNoMoreInteractions(stateHistoryObject);
        verifyNoMoreInteractions(eventImporter);
        verifyNoMoreInteractions(eventExporter);
        verifyNoMoreInteractions(lvtListener);
    }

    /**
     * Verify that if no importer/exporter is registered in the service, pack
     * and unpack return the original event.
     */
    @Test
    public void testNoImporterExporter() {
        Event original = new Event(0);
        Event wrapped = new Event(1);

        Event result;
        result = service.pack(original);
        assertEquals(result, original);
        result = service.unpack(wrapped);
        assertEquals(result, wrapped);

        verifyNoMoreInteractions(startable);
        verifyNoMoreInteractions(flushable);
        verifyNoMoreInteractions(stateHistoryObject);
        verifyNoMoreInteractions(eventImporter);
        verifyNoMoreInteractions(eventExporter);
        verifyNoMoreInteractions(lvtListener);
    }

    /**
     * Verify services
     */
    @Test
    public void testServices() {
        service.register(startable);
        service.start(1L);
        service.stop(2L);
        service.join();
        verify(startable).start(1L);
        verify(startable).stop(2L);
        verify(startable).join();

        service.register(flushable);
        service.flush(3L);
        verify(flushable).flush(3L);

        service.register(stateHistoryObject);
        service.save(4L);
        service.rollback(4L);
        service.commit(4L);
        verify(stateHistoryObject).save(4L);
        verify(stateHistoryObject).rollback(4L);
        verify(stateHistoryObject).commit(4L);

        service.register(lvtListener);
        service.update(5L);
        verify(lvtListener).update(5L);

        verifyNoMoreInteractions(startable);
        verifyNoMoreInteractions(flushable);
        verifyNoMoreInteractions(stateHistoryObject);
        verifyNoMoreInteractions(eventImporter);
        verifyNoMoreInteractions(eventExporter);
        verifyNoMoreInteractions(lvtListener);
    }
}
