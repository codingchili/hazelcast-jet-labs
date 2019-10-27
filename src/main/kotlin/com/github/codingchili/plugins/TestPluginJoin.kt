package com.github.codingchili.plugins

import com.github.codingchili.model.TestObject
import com.github.codingchili.model.ContextImpl
import com.github.codingchili.process.ProcessPlugin

/**
 * @author Robin Duda
 *
 * Join node, it doesn't have any real effect other than converging the flow. It's not a true
 * join as it won't wait for any forked processes and it will be run multiple times per item.
 *
 * This can probably be altered, with synchronization on the context etc.
 */
class TestPluginJoin : ProcessPlugin<ContextImpl, TestObject> {
    override fun process(context: ContextImpl, item: TestObject): TestObject {
        //println("processing ${item.name}")
        return item
    }
}
