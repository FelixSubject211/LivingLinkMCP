package com.felix.livinglink.infrastructure.mcp

import kotlinx.coroutines.Job
import org.koin.core.annotation.Single

@Single
class McpServerRunner(
    private val serverFactory: McpServerFactory,
    private val transportFactory: StdioMcpTransportFactory,
) {
    suspend fun run() {
        val server = serverFactory.create()
        val transport = transportFactory.create()

        server.createSession(transport)

        val keepAlive = Job()

        server.onClose {
            keepAlive.complete()
        }

        keepAlive.join()
    }
}
