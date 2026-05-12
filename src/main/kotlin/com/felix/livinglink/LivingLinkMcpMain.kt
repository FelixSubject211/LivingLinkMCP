package com.felix.livinglink

import com.felix.livinglink.core.delivery.mcp.server.McpServerRunner
import com.felix.livinglink.core.di.LivingLinkApplication
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
