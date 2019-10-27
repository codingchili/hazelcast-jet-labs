package com.github.codingchili.process.impl

import com.hazelcast.config.Config
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.jet.Jet
import com.hazelcast.jet.JetInstance
import com.hazelcast.jet.config.JetConfig
import java.util.*

/**
 * @author Robin Duda
 *
 * Factory for creating instances of the Jet distributed stream processing engine.
 */
object JetFactory {
    @Transient
    private var jet: JetInstance? = null
    @Transient
    private val id = UUID.randomUUID().toString().substring(0,7)

    /**
     * @return the Jet instance if one exists, otherwise one is created.
     */
    fun jetInstance(): JetInstance {
        if (jet == null) {
            synchronized(javaClass) {
                if (jet == null) {
                    jet = Jet.newJetInstance(JetConfig().setHazelcastConfig(Config().setInstanceName("jet_instance_$id")))
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
