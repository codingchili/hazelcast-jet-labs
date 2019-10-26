package com.github.codingchili.process.impl

import com.github.codingchili.process.ProcessBuilder
import com.github.codingchili.process.ProcessContext
import com.github.codingchili.process.ProcessPlugin
import com.hazelcast.core.IQueue
import com.hazelcast.jet.core.DAG
import com.hazelcast.jet.core.Edge
import com.hazelcast.jet.core.Vertex
import com.hazelcast.jet.core.processor.Processors
import com.hazelcast.jet.core.processor.SourceProcessors
import java.io.Serializable
import java.net.InetAddress
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author Robin Duda
 *
 * Process builder that uses the Hazelcast Jet for distributed stream processing.
 * The builder constructs a directed acyclic graph of plugins which any Object can be
 * passed through.
 */
class JetProcessBuilder<Context : ProcessContext, Item : Serializable>(private val context: Class<Context>) :
    ProcessBuilder<Context, Item> {
    private var processName = UUID.randomUUID().toString() // used to set up a distributed Hazelcast queue source.
    private val started = AtomicBoolean(false)
    private var current: Vertex? = null // the "current" vertex is the one #edge draws new edges from.
    private lateinit var root: Vertex // the root vertex is fed by a Hazelcast queue, it's wired to the first #vertex created.
    private val dag = DAG() // pojo that contains the vertices and edges of the graph.

    // note: this method may only be called once and it has the biggest side effect ever seen.
    //
    override fun setName(processName: String): ProcessBuilder<Context, Item> {
        this.processName = processName

        // the root vertex uses a custom stream source, in this case a Hazelcast queue.
        // funnily enough the Hazelcast queue is not a standard <Sources> but JMS is :P
        root = dag.newVertex(
            processName,
            // a bit ironic how this 6-arg method is named convenient >.<
            SourceProcessors.convenientSourceP<IQueue<Item>, Item, Any>(
                { ctx ->
                    ctx.jetInstance().hazelcastInstance.getQueue<Item>(processName)
                },
                // when items are added to the buffer they are immediately emitted.
                { queue, buffer -> buffer.add(queue.take()) },
                // used for snapshots - noop.
                { queue -> queue },
                // used for snapshots - noop.
                { _, _ -> },
                // used to free up the context, in this case the Hazelcast queue.
                // in this case we don't stop the job so it won't be called (?)
                { it.destroy() },
                // this has some special values, 0 means to only deploy the processor once.
                1,
                // not sure what this controls yet, as buffer#add immediately emits items.
                true
            )
        )
        // as an alternative to a custom stream sourace it's possible to create a
        // StreamSource with the Sources Builder - but I was not able to figure out
        // how it could be used with the core DAG API's, it did work with the Pipeline though.
        return this
    }

    override fun vertex(plugin: Class<out ProcessPlugin<Context, Item>>): ProcessBuilder<Context, Item> {
        val first = current == null
        current = createOrGet(plugin) // sets the "current" vertex from which to draw edges from.

        if (first) {
            // if this is the first vertex being created - draw an edge from the source node.
            dag.edge(Edge.between(root, current!!))
        }

        return this
    }

    private fun createOrGet(plugin: Class<out ProcessPlugin<Context, Item>>): Vertex {
        // check if the vertex exists, if it does set it to the current.
        var vertex: Vertex? = dag.getVertex(plugin.simpleName)

        // local instantiation to avoid member reference / serializing 'this'.
        val pc = createContextInstance()

        // if the vertex does not already exist it's created.
        if (vertex == null) {
            vertex = dag.newVertex(plugin.simpleName, Processors.mapP<Item, Item> { entry ->
                trace(plugin.name) // distributed tracing - pretty cool!

                // create a new instance of the plugin and run it. (could use reflectasm+cache for this)


                val instance = plugin.getConstructor().newInstance()
                instance.process(pc, entry)
            })
        }
        return vertex
    }

    private fun createContextInstance(): Context {
        try {
            // instantiates the context reused between elements in the processor.
            return context.getConstructor().newInstance()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    override fun edge(to: Class<out ProcessPlugin<Context, Item>>): ProcessBuilder<Context, Item> {
        // creates a distributed edge from the currently focused vertex to the given plugin.
        // reusing the vertex if the given plugin already has a vertex created.
        val vertex = createOrGet(to)
        val outbound = dag.getOutboundEdges(current!!.name).size
        val inbound = dag.getInboundEdges(to.simpleName).size
        dag.edge(Edge.from(current!!, outbound).distributed().to(vertex, inbound))
        return this
    }

    override fun submit(item: Item): CompletableFuture<Void> {
        // adds a new item on the Hazelcast queue, this is emitted immediately onto the DAG.
        JetFactory.hazelInstance().getQueue<Any>(processName).add(item)

        // check if the Jet engine is started or not.
        return if (!started.getAndSet(true)) {
            JetFactory.jetInstance().newJob(dag).future
        } else {
            // for streaming jobs the future will never complete which is a bit sad.
            // batch jobs will probably be ineffective as they need to be deployed
            // once per item emitted.
            CompletableFuture.completedFuture(null)
        }
    }

    override fun shutdown() {
        // should probably only shut down the Job - but this is just lab software right.
        JetFactory.jetInstance().shutdown()
        JetFactory.hazelInstance().shutdown()
    }

    companion object {
        public const val DISTRIBUTED_TRACING = "hz-tracing"
    }
}


fun trace(plugin: String) {
    // publish a trace event on the Hazelcast cluster topic :D
    JetFactory.hazelInstance()
        .getTopic<Any>(JetProcessBuilder.DISTRIBUTED_TRACING)
        .publish("running plugin $plugin on machine ${InetAddress.getLocalHost().hostName}")
}