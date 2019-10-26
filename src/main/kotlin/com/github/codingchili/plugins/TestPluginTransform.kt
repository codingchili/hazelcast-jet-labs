package com.github.codingchili.plugins

import com.github.codingchili.model.ObjectForProcessing
import com.github.codingchili.process.ProcessPlugin
import com.github.codingchili.model.ProcessContextImpl

/**
 * @author Robin Duda
 *
 * This is a plugin that performs some modification on the test object being processed.
 */
class TestPluginTransform :
    ProcessPlugin<ProcessContextImpl, ObjectForProcessing> {
    override fun process(context: ProcessContextImpl, item: ObjectForProcessing): ObjectForProcessing {
        // the context in this case contains a method for transforming the given item. cool.
        return context.transform(item)
    }
}