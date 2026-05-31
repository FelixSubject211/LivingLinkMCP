package com.felix.livinglink.composeapp

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return platform.name
    }
}