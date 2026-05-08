package com.felix.livinglink

import com.felix.livinglink.di.livingLinkModule
import com.felix.livinglink.mcp.McpRunner
import io.modelcontextprotocol.kotlin.sdk.server.Server
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin

suspend fun main() {
    System.setProperty(
        "kotlin.logging.internal.platform.kotlinLoggingStartupMessageEnabled",
        "false",
    )

    startKoin {
        modules(livingLinkModule)
    }

    val server = getKoin().get<Server>()

    McpRunner.run(server)
}
