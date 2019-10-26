package com.github.codingchili.process

import java.io.Serializable

/**
 * @author Robin Duda
 *
 * The base context is empty, it's pretty nice to keep it that way.
 * Contexts may do whatever they want in that case, it's just used for the generics.
 */
interface ProcessContext : Serializable
