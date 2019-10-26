package com.github.codingchili.plugins

import com.github.codingchili.model.ContextImpl
import com.github.codingchili.model.TestObject
import com.github.codingchili.process.ProcessPlugin

/**
 * @author Robin Duda
 *
 * This is a plugin that performs some modification on the test object being processed.
 */
class TestPluginTransform : ProcessPlugin<ContextImpl, TestObject> {
    override fun process(context: ContextImpl, item: TestObject): TestObject {
        // the context in this case contains a method for transforming the given item. cool.
        return context.transform(item)
    }
}
