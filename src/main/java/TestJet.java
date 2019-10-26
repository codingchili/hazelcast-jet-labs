import com.hazelcast.core.Hazelcast;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;

/**
 * @author Robin Duda
 */
public class TestJet {
    // run a complete case object through?
    // limits to one process per obj.
    // multiple charts per object vs one dag to rule them all?
    // special nodes for special objects
    // fork to deal with child objects
    // join up
    // waiting in DAG vs waiting between DAG's?
    // creating a process every time it's asynchronous
    // how to handle synchronous?
    // blocking join on the request
    // cases are small, text content of case tree 1MB = 100 cases, 50.000 words ea.
    // caseTreeBuilder, caseTreePersistor, caseTreeDirty()
    // persist at any stage, at end or partially
    // case stages, stored outside of dag whenever in externalWaiting
    // prepare for shutdown: block new jobs, wait for current jobs
    // need plugins at each vertex
    // jobs can spawn other jobs
    // jobs can wait for other jobs
    // jobs can wait for external
    // support for building DAG's from chart.xml
    // files/json/ws: parse once then bytes serialized
    // create new case objects on existing objects in DAG stream: server call on tree persist
    // local vs distributed edges
    // https://docs.hazelcast.org/docs/jet/0.3.1/manual/Understanding_Jet_Architecture_and_API/Edge/Forwarding_Patterns.html
    // forks: there is no combined fork/filter? YES: see Edge Ordinal
    // PIPELINE = EASY, DAG = ADVANCED
    // dags make simpler processes, no loops.
    // http://viz-js.com/
    // run transforms on case object
    // pause the whole job vs pause/unpause a single node, use messageing
    // dag error checking at build time - fast to test yo

    public static void main(String[] args) throws InterruptedException {
        //instance();
        process();
    }

    private static void instance() {
        //JetInstance jet = Jet.newJetInstance();
    }

    private static void process() throws InterruptedException {
        ProcessBuilder<ProcessContextImpl, MyStupidPojo> process = ProcessFactory.create(ProcessContextImpl.class);
        try {
            process.setName("process-engine-2.0");
            process.vertex(TestPluginTransform.class)
                    .edge(TestPluginSave.class);

            process.submit(new MyStupidPojo("testing 1.0"));
            Thread.sleep(2000);
            process.submit(new MyStupidPojo("testing 2.0"));
            Thread.sleep(2000);
            process.submit(new MyStupidPojo("testing 3.0"));
            Thread.sleep(5000);
        } finally {
            process.shutdown();
        }
    }
}
