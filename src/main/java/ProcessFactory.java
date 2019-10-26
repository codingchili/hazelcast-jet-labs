import java.io.Serializable;

/**
 * @author Robin Duda
 *
 * Wrapper class for hazelcast/dag implementation
 */
public class ProcessFactory {

    /**
     *
     * @param context
     * @param <C>
     * @param <E>
     * @return
     */
    public static <C extends ProcessContext, E extends Serializable> ProcessBuilder<C, E> create(Class<C> context) {
        return new JetProcessBuilder<>(context);
    }

}
