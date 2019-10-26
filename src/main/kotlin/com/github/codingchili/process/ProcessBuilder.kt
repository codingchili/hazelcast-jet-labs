package com.github.codingchili.process

import java.io.Serializable
import java.util.concurrent.CompletableFuture

/**
 * @author Robin Duda
 *
 * Interface used to build a process chart. (like a DAG with Jet)
 */
interface ProcessBuilder<Context : ProcessContext, Item : Serializable> {

    /**
     * @param processName the name of the process.
     * @return fluent
     */
    fun setName(processName: String): ProcessBuilder<Context, Item>

    /**
     * Creates a new vertex if one does not already exist for the given plugin. Plugins
     * are used to uniquely identify the vertex so a single plugin may only occur once
     * in the process. If the same plugin is added twice - the original vertex will be
     * set to the current vertx, allowing to call #edge to draw more edges from it.
     *
     * @param plugin the plugin implementation that is processing items on this vertex.
     * @return fluent
     */
    fun vertex(plugin: Class<out ProcessPlugin<Context, Item>>): ProcessBuilder<Context, Item>

    /**
     * Creates an edge from the last added vertex to the given plugin/vertex. If the given
     * plugin is not attached to a vertex - a vertex will be created.
     * @param to the vertex to create an edge to.
     * @return fluent
     */
    fun edge(to: Class<out ProcessPlugin<Context, Item>>): ProcessBuilder<Context, Item>

    /**
     * @param item
     * @return future completed when the job is done (for streaming jobs they are never done.)
     */
    fun submit(item: Item): CompletableFuture<Void>

    /**
     * Shuts down the processing engine. (tbd: only shuts down the job and not the engine)
     */
    fun shutdown()
}
