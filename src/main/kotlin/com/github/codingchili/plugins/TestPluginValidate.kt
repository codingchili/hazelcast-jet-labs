package com.github.codingchili.plugins

import com.github.codingchili.model.TestObject
import com.github.codingchili.model.ContextImpl
import com.github.codingchili.process.ProcessPlugin

/**
 * @author Robin Duda
 *
 * Some test plugin to validate the contents, not sure how to stop the processing
 * of an item in the stream yet :P
 */
class TestPluginValidate : ProcessPlugin<ContextImpl, TestObject> {
    override fun process(context: ContextImpl, item: TestObject): TestObject {
        if (item.name.contains("!#/bin/bash")) {
            item.name = "invalid"
        }
        return item
    }
}
