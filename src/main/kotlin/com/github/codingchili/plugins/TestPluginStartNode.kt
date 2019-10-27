package com.github.codingchili.plugins

import com.github.codingchili.model.TestObject
import com.github.codingchili.model.ContextImpl
import com.github.codingchili.process.ProcessPlugin

/**
 * @author Robin Duda
 *
 * The start node, currently it's not possible to connect multiple vertices directly
 * to the stream source node.
 */
class TestPluginStartNode : ProcessPlugin<ContextImpl, TestObject> {
    override fun process(context: ContextImpl, item: TestObject): TestObject {
        //println("started processing ${item.name}")
        return item
    }
}
