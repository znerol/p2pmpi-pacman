package deism.adapter;

import deism.core.Event;
import deism.core.StatefulEventGenerator;
import deism.ipc.base.EventImporter;

/**
 * Adapter class which lets an {@link deism.ipc.base.EventImporter} intercept
 * and modify messages coming from another simulation island thru the adapted
 * {@link deism.core.StatefulEventGenerator}.
 */
public class ExternalEventGeneratorAdapter implements StatefulEventGenerator {
    private final StatefulEventGenerator generator;
    private final EventImporter importer;

    public ExternalEventGeneratorAdapter(StatefulEventGenerator generator,
            EventImporter importer) {
        this.generator = generator;
        this.importer = importer;
    }

    @Override
    public Event poll() {
        Event result = generator.poll();

        if (result != null) {
            result = importer.unpack(result);
        }

        return result;
    }
}
