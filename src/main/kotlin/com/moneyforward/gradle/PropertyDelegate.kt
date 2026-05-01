package com.moneyforward.gradle

/**
 * Abstracts property resolution across different Gradle contexts (project, settings, etc.).
 */
interface PropertyDelegate {
    /**
     * Resolves a property by name, returning null if not found.
     *
     * @param name The property name to look up.
     */
    fun resolve(name: String): Any?

    /**
     * Resolves a property by name and casts the result to [T], returning null if not found.
     *
     * @param name The property name to look up.
     */
    fun <T> resolveTo(name: String): T? {
        return resolve(name) as T?
    }
}