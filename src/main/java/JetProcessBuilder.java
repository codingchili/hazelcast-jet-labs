import com.hazelcast.core.DistributedObject;
import com.hazelcast.jet.core.DAG;
import com.hazelcast.jet.core.Edge;
import com.hazelcast.jet.core.Vertex;
import com.hazelcast.jet.core.processor.Processors;
import com.hazelcast.jet.core.processor.SourceProcessors;
import com.hazelcast.jet.pipeline.SourceBuilder;
import com.hazelcast.jet.pipeline.StreamSource;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Robin Duda
 * <p>
 * Builds a Hazelcast JET process.
 * <p>
 * -- plugin cache, distributed tracing, context caching
 */
public class JetProcessBuilder<C extends ProcessContext, E extends Serializable> implements ProcessBuilder<C, E> {
    private static final String SOURCE_NODE = "source";
    private static final String STREAM_NAME = "hz-queue";
    private static final String DISTRIBUTED_TRACING = "hz-tracing";
    private AtomicBoolean started = new AtomicBoolean(false);
    private String processName = UUID.randomUUID().toString();
    private Class<C> context;
    private DAG dag = new DAG();
    private Vertex current;
    private Vertex root;

    public JetProcessBuilder(Class<C> context) {
        this.context = context;
        String processName = this.processName;

        root = dag.newVertex(SOURCE_NODE, SourceProcessors.convenientSourceP(ctx -> {
                    return ctx.jetInstance().getHazelcastInstance().getQueue(processName);
                }, (queue, buffer) -> {
                    buffer.add(queue.take());
                },
                (queue) -> queue,     // snapshot
                (queue, list) -> { }, // snapshot restore
                DistributedObject::destroy,
                1,
                true
        ));
    }

    private static <E> StreamSource<E> getSource(String processName) {
        return SourceBuilder.stream(STREAM_NAME, (ctx) -> {
            return ctx.jetInstance().getHazelcastInstance().<E>getQueue(processName);
        }).<E>fillBufferFn((context, buffer) -> {
            buffer.add(context.take());
        }).build();
    }

    @Override
    public ProcessBuilder<C, E> setName(String processName) {
        //this.processName = processName;
        return this;
    }

    @Override
    public ProcessBuilder<C, E> vertex(Class<? extends ProcessPlugin<C, E>> from) {
        boolean first = (current == null);
        current = createOrGet(from);

        if (first) {
            dag.edge(Edge.between(root, current));
        }

        return this;
    }

    private Vertex createOrGet(Class<? extends ProcessPlugin<C, E>> from) {
        Vertex vertex = dag.getVertex(from.getName());
        C pc = createContextInstance(); // local instantiation to avoid member reference / serializing 'this'.

        if (vertex == null) {
            vertex = dag.newVertex(from.getName(), Processors.<E, E>mapP(entry -> {
                JetFactory.hazelInstance().getTopic(DISTRIBUTED_TRACING)
                        .publish("running plugin " + from.getSimpleName() + " on machine " + InetAddress.getLocalHost().getHostName());

                ProcessPlugin<C, E> plugin = from.newInstance();
                return plugin.process(pc, entry);
            }));
        }
        return vertex;
    }

    private C createContextInstance() {
        try {
            return context.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProcessBuilder<C, E> edge(Class<? extends ProcessPlugin<C, E>> to) {
        dag.edge(Edge.from(current).distributed().to(createOrGet(to)));
        return this;
    }

    @Override
    public CompletableFuture<Void> submit(E item) {
        JetFactory.hazelInstance().getQueue(processName).add(item);

        if (!started.getAndSet(true)) {
            return JetFactory.jetInstance().newJob(dag).getFuture();
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }

    @Override
    public void shutdown() {
        // kill all processes idk.
    }
}
