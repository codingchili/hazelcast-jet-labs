package com.github.codingchili.process.impl

import com.github.codingchili.process.ProcessBuilder
import com.github.codingchili.process.ProcessContext
import java.io.Serializable

/**
 * @author Robin Duda
 *
 * Provides the builder implementation that wraps the low level core API's of Hazelcast Jet.
 */
object ProcessFactory {

    /**
     * Creates a process builder used for creating new process charts/graphs for stream processing.
     * @param context the context class that will be injected into each plugin, can provide common
     * functionality - access to API's or a distributed cache etc.
     * @param <C> the type of the context implementation that is used.
     * @param <E> the type of the items that are being processed in the graph.
     * @return a new process builder.
     */
    fun <C : ProcessContext, E : Serializable> create(context: Class<C>): ProcessBuilder<C, E> {
        return JetProcessBuilder(context)
    }
}
