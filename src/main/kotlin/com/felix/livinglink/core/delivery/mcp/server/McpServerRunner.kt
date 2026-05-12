package com.felix.livinglink.core.delivery.mcp.server

import com.felix.livinglink.core.database.mongo.MongoClientProvider
import kotlinx.coroutines.Job
import org.koin.core.annotation.Single

@Single
class McpServerRunner(
    private val serverFactory: McpServerFactory,
    private val transportFactory: StdioMcpTransportFactory,
    private val mongoClientProvider: MongoClientProvider,
) {
    suspend fun run() {
        val server = serverFactory.create()
        val transport = transportFactory.create()
        val keepAlive = Job()

        try {
            server.createSession(transport)

            server.onClose {
                keepAlive.complete()
            }

            keepAlive.join()
        } finally {
            mongoClientProvider.close()
        }
    }
}
