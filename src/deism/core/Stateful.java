package deism.core;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for sources and sinks which depend on internal state
 * 
 * <p>
 * Annotated {@link deism.core.EventSource} instances will be wrapped into a
 * {@link deism.stateful.TimewarpEventSourceAdapter} when added to a
 * DiscreteEventProcess via
 * {@link deism.process.DefaultProcessBuilder#add(EventSource)} unless they
 * implement {@link deism.stateful.StateHistory}. Likewise an annotated
 * {@link deism.core.EventSink} instance will be wrapped into a
 * {@link deism.stateful.TimewarpEventSinkAdapter} when added to a
 * DiscreteEventProcess via
 * {@link deism.process.DefaultProcessBuilder#add(EventSink)} unless they
 * implement {@link deism.stateful.StateHistory}.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Stateful {
}
