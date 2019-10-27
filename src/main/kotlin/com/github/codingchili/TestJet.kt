package com.github.codingchili

import com.github.codingchili.model.ContextImpl
import com.github.codingchili.model.TestObject
import com.github.codingchili.plugins.*
import com.github.codingchili.process.impl.JetFactory
import com.github.codingchili.process.impl.JetProcessBuilder.Companion.DISTRIBUTED_TRACING
import com.github.codingchili.process.impl.ProcessFactory
import java.util.logging.Level

/**
 * @author Robin Duda
 *
 * An example on how the Hazelcast Jet API's can be used by consumers with a thin wrapper.
 */
class TestJet

fun main(args: Array<String>) {
    when {
        args.contains("--instance") -> { println("starting a passive instance"); instance() }
        args.contains("--process") -> { println("starting a generator instance"); process() }
        else -> println("use '--instance' to start a passive instance or '--process' to start processing items.")
    }
}

fun instance() {
    JetFactory.jetInstance()
}

fun process() {
    // use a factory to construct the process builder to decouple ourselves from Jet, the
    // process context can be used to provide contextual functionality to the processing plugins.
    // for example attaching properties to the process or exposing client API's.
    val process = ProcessFactory.create<ContextImpl, TestObject>(
        ContextImpl::class.java
    )
    try {
        // set the name of the process.
        process.setName("MessageSource")

        // the first vertx will be attached to the stream source - which is fed by submitting items to the process.
        // by calling edge an edge is created from the last #vertex invocation to the given plugin.
        // the DAG is validated before the job is deployed.
        process.vertex(TestPluginStartNode::class.java)
            .edge(TestPluginTransform::class.java)
            .edge(TestPluginValidate::class.java)

        // one of the fork paths
        process.vertex(TestPluginTransform::class.java)
            .edge(TestPluginJoin::class.java)

        // the second fork path
        process.vertex(TestPluginValidate::class.java)
            .edge(TestPluginJoin::class.java)

        // join node, forward to end node "save".
        process.vertex(TestPluginJoin::class.java)
            .edge(TestPluginSave::class.java)

        // distributed tracing, can be used for realtime visualizations.
        JetFactory.hazelInstance().getTopic<String>(DISTRIBUTED_TRACING)
            .addMessageListener {
                JetFactory.hazelInstance().loggingService.getLogger("logging")
                    .log(Level.INFO, it.messageObject)
            }

        for (i in 0..100) {
            process.submit(TestObject("test_$i"))
            Thread.sleep(1000)
        }

    } finally {
        // shut down the jet instance to exit the jvm.
        process.shutdown()
    }
}