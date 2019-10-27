package com.github.codingchili.model

import com.github.codingchili.process.ProcessContext
import com.github.codingchili.process.impl.JetFactory
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.jet.JetInstance

/**
 * @author Robin Duda
 *
 * A very simple context, this is instantiated once per processor and reused during the
 * lifetime of the job, the context typically contains job configuration and helpful utilities.
 */
class ContextImpl : ProcessContext {

    /**
     * Applies a simple transformation on the object being processed.
     */
    fun transform(pojo: TestObject): TestObject {
        pojo.name = pojo.name + "_TRANSFORMED"
        return pojo
    }

    /**
     * Performs a fake save operation on the element being processed.
     */
    fun save(pojo: TestObject) {
        //println("saved pojo $pojo")
    }

    /**
     * Provide access to the hazelcast instance from plugins.
     */
    fun hazel(): HazelcastInstance {
        return JetFactory.hazelInstance()
    }

    /**
     * Provide access to the jet instance from plugins.
     */
    fun jet(): JetInstance {
        return JetFactory.jetInstance()
    }
}
