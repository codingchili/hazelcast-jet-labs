package com.github.codingchili.plugins

import com.github.codingchili.model.ObjectForProcessing
import com.github.codingchili.model.ProcessContextImpl
import com.github.codingchili.process.ProcessPlugin

/**
 * @author Robin Duda
 *
 * Some test plugin to validate the contents, not sure how to stop the processing
 * of an item in the stream yet :P
 */
class TestPluginValidate :
    ProcessPlugin<ProcessContextImpl, ObjectForProcessing> {
    override fun process(context: ProcessContextImpl, item: ObjectForProcessing): ObjectForProcessing {
        if (item.name.contains("!#/bin/bash")) {
            item.name = "invalid"
        }
        return item
    }
}
