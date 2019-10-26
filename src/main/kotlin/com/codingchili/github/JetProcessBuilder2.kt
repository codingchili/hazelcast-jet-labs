package com.codingchili.github

import JetFactory
import ProcessContext
import ProcessPlugin
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
 *
 * Builds a Hazelcast JET process.
 *
 *
 * -- plugin cache, distributed tracing, context caching
 */
class JetProcessBuilder2<C : ProcessContext, E : Serializable>(private val context: Class<C>) : ProcessBuilder<C, E> {
    private val started = AtomicBoolean(false)
    private val processName = UUID.randomUUID().toString()
    private val dag = DAG()
    private var current: Vertex? = null
    private val root: Vertex

    init {
        val processName = this.processName

        root = dag.newVertex(
            SOURCE_NODE,
            SourceProcessors.convenientSourceP<IQueue<E>, E, Any>({ ctx ->
                ctx.jetInstance().hazelcastInstance.getQueue<E>(processName)
            },
                { queue, buffer -> buffer.add(queue.take()) },
                { queue -> queue }, // snapshot
                { queue, list -> }, // snapshot restore
                { it.destroy() },
                1,
                true
            )
        )
    }

    override fun setName(processName: String): ProcessBuilder<C, E> {
        //this.processName = processName;
        return this
    }

    override fun vertex(from: Class<out ProcessPlugin<C, E>>): ProcessBuilder<C, E> {
        val first = current == null
        current = createOrGet(from)

        if (first) {
            dag.edge(Edge.between(root, current!!))
        }

        return this
    }

    private fun createOrGet(from: Class<out ProcessPlugin<C, E>>): Vertex {
        var vertex: Vertex? = dag.getVertex(from.name)
        val pc = createContextInstance() // local instantiation to avoid member reference / serializing 'this'.

        if (vertex == null) {
            vertex = dag.newVertex(from.name, Processors.mapP<E, E> { entry ->
                JetFactory.hazelInstance()
                    .getTopic<Any>(DISTRIBUTED_TRACING)
                    .publish("running plugin " + from.simpleName + " on machine " + InetAddress.getLocalHost().hostName)

                val plugin = from.newInstance()
                plugin.process(pc, entry)
            })
        }
        return vertex
    }

    private fun createContextInstance(): C {
        try {
            return context.newInstance()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    override fun edge(to: Class<out ProcessPlugin<C, E>>): ProcessBuilder<C, E> {
        dag.edge(Edge.from(current!!).distributed().to(createOrGet(to)))
        return this
    }

    override fun submit(item: E): CompletableFuture<Void> {
        JetFactory.hazelInstance().getQueue<Any>(processName).add(item)

        return if (!started.getAndSet(true)) {
            JetFactory.jetInstance().newJob(dag).future
        } else {
            CompletableFuture.completedFuture(null)
        }
    }

    override fun shutdown() {
        // kill all processes idk.
    }

    companion object {
        private val SOURCE_NODE = "source"
        private val STREAM_NAME = "hz-queue"
        private val DISTRIBUTED_TRACING = "hz-tracing"
    }
}
