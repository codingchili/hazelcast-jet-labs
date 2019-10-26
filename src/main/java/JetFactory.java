import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;

/**
 * @author Robin Duda
 *
 */
public class JetFactory {
    private static transient JetInstance jet;

    /**
     *
     * @return
     */
    public static JetInstance jetInstance() {
        if (jet == null) {
            synchronized (JetProcessBuilder.class) {
                if (jet == null) {
                    jet = Jet.newJetInstance();
                }
            }
        }
        return jet;
    }

    /**
     *
     * @return
     */
    public static HazelcastInstance hazelInstance() {
        return jetInstance().getHazelcastInstance();
    }
}
