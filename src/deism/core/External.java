package deism.core;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for sources and sinks which are communication endpoints.
 * 
 * <p>
 * Annotated {@link deism.core.StatefulEventGenerator} instances will be wrapped
 * into a {@link deism.adapter.ExternalEventGeneratorAdapter} when added to a
 * DiscreteEventProcess via
 * {@link deism.process.DefaultProcessBuilder#add(StatefulEventGenerator)}.
 * Likewise an annotated {@link deism.core.EventSink} instance will be wrapped
 * into a {@link deism.adapter.ExternalEventSinkAdapter} when added to a
 * DiscreteEventProcess via
 * {@link deism.process.DefaultProcessBuilder#add(EventSink)}.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface External {

}
