package com.moneyforward.gradle

interface PropertyDelegate {
    fun resolve(name: String): Any?
    fun <T> resolveTo(name: String): T? {
        return resolve(name) as T?
    }
}