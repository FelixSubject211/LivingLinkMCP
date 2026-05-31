package com.felix.livinglink.composeapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform