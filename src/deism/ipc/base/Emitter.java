package deism.ipc.base;

/**
 * Source of ipc messages
 *
 * Classes implementing Emitter do generate ipc messages of the given type T
 * during execution by sending them to an Endpoint of type T which gets hooked
 * up to instances of this class in the simulation setup.
 */
public interface Emitter<T> {
    public Endpoint<T> getEndpoint(Class<T> clazz);
    public void setEndpoint(Endpoint<T> endpoint);
}
