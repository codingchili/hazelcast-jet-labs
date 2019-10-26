import java.io.Serializable;

/**
 * @author Robin Duda
 */
@FunctionalInterface // because lambdas?
public interface ProcessPlugin<C extends ProcessContext, E extends Serializable> {

    /**
     * process the item and get a result
     * @param item
     * @return
     */
    E process(C context, E item);
}
