package deism.adapter;

import deism.core.Event;
import deism.core.EventSink;
import deism.ipc.base.EventExporter;

/**
 * Adapter class which lets an {@link deism.ipc.base.EventExporter} intercept
 * and modify messages leaving in direction of another simulation island thru
 * the adapted {@link deism.core.EventSink}.
 */
public class ExternalEventSinkAdapter implements EventSink {
    private final EventSink sink;
    private final EventExporter exporter;

    public ExternalEventSinkAdapter(EventSink sink, EventExporter exporter) {
        this.sink = sink;
        this.exporter = exporter;
    }

    @Override
    public void offer(Event event) {
        event = exporter.pack(event);

        if (event != null) {
            sink.offer(event);
        }
    }
}
