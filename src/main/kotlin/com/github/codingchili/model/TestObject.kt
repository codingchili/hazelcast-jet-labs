package com.github.codingchili.model

import java.io.Serializable
import java.util.HashSet

/**
 * @author Robin Duda
 *
 * This is a sample test POJO. It could contain a hierarchy of cases, files or a broker message.
 * Each vertex plugin is responsible for updating and persisting the object as deemed fit.
 *
 * It is recommended to load the whole case tree in a POJO as the text content is typically
 * very small. This allows for extremely efficient modifications of the case during processing.
 */
class TestObject(name: String) : Serializable {
    var ref = "$/root/cases/stuff/"
    var name = "wowza"
    var objs: MutableSet<String> = HashSet()

    init {
        objs.add("one")
        objs.add("two")
        objs.add("three")
    }

    init {
        this.name = name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "name: $name, ref: $ref, objs: $objs"
    }

    override fun equals(other: Any?): Boolean {
        return this === other
    }
}
