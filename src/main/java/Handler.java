/**
 * @author Robin Duda
 */
@FunctionalInterface
public interface Handler {
    void setHandler(Runnable runnable);
}
