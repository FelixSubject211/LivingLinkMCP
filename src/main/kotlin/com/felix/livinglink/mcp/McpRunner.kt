package com.felix.livinglink.mcp

import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import kotlinx.coroutines.Job
import kotlinx.io.asSink
import kotlinx.io.asSource
import kotlinx.io.buffered

object McpRunner {
    suspend fun run(server: Server) {
        val transport = StdioServerTransport(
            inputStream = System.`in`.asSource().buffered(),
            outputStream = System.out.asSink().buffered()
        )

        server.createSession(transport)

        val keepAlive = Job()
        server.onClose {
            keepAlive.complete()
        }
        keepAlive.join()
    }
}