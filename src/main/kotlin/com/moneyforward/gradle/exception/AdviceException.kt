package com.moneyforward.gradle.exception

abstract class AdviceException(
    message: String,
    cause: Exception? = null,
) : Exception(message, cause) {
    open fun getAdvice(): String? = message
}
