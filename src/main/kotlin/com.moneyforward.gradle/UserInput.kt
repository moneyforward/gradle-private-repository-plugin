package com.moneyforward.gradle

import java.util.*

internal interface UserInput {
    fun prompt(text: String): String
    fun promptPassword(text: String): String

    companion object {
        fun create(): UserInput {
            return try {
                Console()
            } catch (ex: NullPointerException) {
                KotlinDSL()
            }
        }
    }

    private class Console : UserInput {
        private val console = Objects.requireNonNull(System.console())

        override fun prompt(text: String): String {
            return console.readLine(text)
        }

        override fun promptPassword(text: String): String {
            return String(console.readPassword(text))
        }
    }

    private class KotlinDSL : UserInput {
        override fun prompt(text: String): String {
            println(text)
            return readln()
        }

        override fun promptPassword(text: String): String {
            println(text)
            return readln()
        }
    }
}

