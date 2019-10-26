import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

/**
 * @author Robin Duda
 */
public interface ProcessBuilder<C extends ProcessContext, E extends Serializable> {

    ProcessBuilder<C, E> setName(String processName);

    /**
     * @param mapper
     * @return
     */
    ProcessBuilder<C, E> vertex(Class<? extends ProcessPlugin<C, E>> from);

    /**
     *
     * @param to
     * @return
     */
    ProcessBuilder<C, E> edge(Class<? extends ProcessPlugin<C, E>> to);

    /**
     * @param item
     */
    CompletableFuture<Void> submit(E item);

    /**
     *
     */
    void shutdown();
}
