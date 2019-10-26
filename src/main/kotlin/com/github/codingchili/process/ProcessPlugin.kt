package com.github.codingchili.process

import java.io.Serializable

/**
 * @author Robin Duda
 *
 * This is the plugin interface for the plugins that execute on the vertices/nodes.
 */
@FunctionalInterface
interface ProcessPlugin<C : ProcessContext, E : Serializable> {

    /**
     * Process the given item from the stream and return it as it should be
     * forwarded to the next plugin vertex.
     *
     * @param context the processing context, contains helper methods etc.
     * @param item the item to apply processing to, a message, file, case etc.
     * @return the item to be forwarded to the next vertex in the graph.
     */
    fun process(context: C, item: E): E
}
