package com.github.codingchili.process.impl

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.jet.Jet
import com.hazelcast.jet.JetInstance

/**
 * @author Robin Duda
 *
 * Factory for creating instances of the Jet distributed stream processing engine.
 */
object JetFactory {
    @Transient
    private var jet: JetInstance? = null

    /**
     * @return the Jet instance if one exists, otherwise one is created.
     */
    fun jetInstance(): JetInstance {
        if (jet == null) {
            synchronized(javaClass) {
                if (jet == null) {
                    jet = Jet.newJetInstance()
                }
            }
        }
        return jet!!
    }

    /**
     * @return the hazelcast instance associated with the current Jet instance.
     */
    fun hazelInstance(): HazelcastInstance {
        return jetInstance().hazelcastInstance
    }
}
