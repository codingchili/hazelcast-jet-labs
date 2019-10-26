package com.github.codingchili.plugins

import com.github.codingchili.model.TestObject
import com.github.codingchili.process.ProcessPlugin
import com.github.codingchili.model.ContextImpl

/**
 * @author Robin Duda
 *
 * A simple test plugin that uses the context to save the item being processed.
 */
class TestPluginSave : ProcessPlugin<ContextImpl, TestObject> {
    override fun process(context: ContextImpl, item: TestObject): TestObject {
        context.save(item)

        // the item returned will be passed to the next plugin, which allows the whole
        // item to be replaced. This is equivalent to the ".map' function.
        return item
    }
}
