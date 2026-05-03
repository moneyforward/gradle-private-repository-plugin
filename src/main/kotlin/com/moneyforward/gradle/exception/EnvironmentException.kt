package com.moneyforward.gradle.exception

class EnvironmentException(
    message: String,
    cause: Exception? = null,
) : AdviceException(message, cause)
