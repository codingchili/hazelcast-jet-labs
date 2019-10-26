package com.github.codingchili.model

import com.github.codingchili.process.ProcessContext

/**
 * @author Robin Duda
 *
 * A very simple context, this is instantiated once per processor and reused during the
 * lifetime of the job, the context typically contains job configuration and helpful utilities.
 */
class ProcessContextImpl : ProcessContext {

    /**
     * Applies a simple transformation on the object being processed.
     */
    fun transform(pojo: ObjectForProcessing): ObjectForProcessing {
        pojo.name = pojo.name + "_TRANSFORMED"
        return pojo
    }

    /**
     * Performs a fake save operation on the element being processed.
     */
    fun save(pojo: ObjectForProcessing) {
        println("saved pojo $pojo")
    }
}
