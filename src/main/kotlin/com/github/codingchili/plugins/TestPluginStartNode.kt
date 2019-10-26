package com.github.codingchili.plugins

import com.github.codingchili.model.ObjectForProcessing
import com.github.codingchili.model.ProcessContextImpl
import com.github.codingchili.process.ProcessPlugin

/**
 * @author Robin Duda
 *
 * The start node, currently it's not possible to connect multiple vertices directly
 * to the stream source node.
 */
class TestPluginStartNode :
    ProcessPlugin<ProcessContextImpl, ObjectForProcessing> {
    override fun process(context: ProcessContextImpl, item: ObjectForProcessing): ObjectForProcessing {
        println("started processing ${item.name}")
        return item
    }
}
