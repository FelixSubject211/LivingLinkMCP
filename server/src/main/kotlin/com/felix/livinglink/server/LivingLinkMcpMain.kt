package com.felix.livinglink.server

import com.felix.livinglink.server.core.di.LivingLinkApplication
import org.koin.java.KoinJavaComponent.getKoin
import org.koin.plugin.module.dsl.startKoin

suspend fun main() {
    System.setProperty(
        "kotlin.logging.internal.platform.kotlinLoggingStartupMessageEnabled",
        "false",
    )

    startKoin<LivingLinkApplication>()

    val runner = getKoin().get<McpServerRunner>()

    runner.run()
}
