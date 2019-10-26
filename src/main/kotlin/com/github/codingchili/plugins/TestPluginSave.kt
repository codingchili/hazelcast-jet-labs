package com.github.codingchili.plugins

import com.github.codingchili.model.ObjectForProcessing
import com.github.codingchili.process.ProcessPlugin
import com.github.codingchili.model.ProcessContextImpl

/**
 * @author Robin Duda
 *
 * A simple test plugin that uses the context to save the item being processed.
 */
class TestPluginSave :
    ProcessPlugin<ProcessContextImpl, ObjectForProcessing> {
    override fun process(context: ProcessContextImpl, item: ObjectForProcessing): ObjectForProcessing {
        context.save(item)

        // the item returned will be passed to the next plugin, which allows the whole
        // item to be replaced. This is equivalent to the ".map' function.
        return item
    }
}
