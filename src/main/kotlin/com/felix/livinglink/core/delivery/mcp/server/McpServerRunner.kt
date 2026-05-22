package com.felix.livinglink.core.delivery.mcp.server

import com.felix.livinglink.core.infrastructure.mongo.MongoClientProvider
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.routing.routing
import io.modelcontextprotocol.kotlin.sdk.server.mcpStreamableHttp
import kotlinx.coroutines.Job
import org.koin.core.annotation.Single
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("McpServerRunner")

@Single
class McpServerRunner(
    private val serverFactory: McpServerFactory,
    private val transportFactory: StdioMcpTransportFactory,
    private val mongoClientProvider: MongoClientProvider,
    private val transportSettings: McpTransportSettings,
    private val apiKeySettings: McpApiKeySettings,
    private val stdioUserSettings: McpStdioUserSettings,
) {
    suspend fun run() {
        try {
            when (transportSettings.transport) {
                McpTransport.Stdio -> runStdio()
                McpTransport.Http -> runHttp()
            }
        } finally {
            mongoClientProvider.close()
        }
    }

    private suspend fun runStdio() {
        val server =
            serverFactory.create(
                user = stdioUserSettings.user,
            )
        val transport = transportFactory.create()
        val keepAlive = Job()

        server.createSession(transport)

        server.onClose {
            keepAlive.complete()
        }

        keepAlive.join()
    }

    private fun runHttp() {
        embeddedServer(
            factory = CIO,
            host = transportSettings.httpHost,
            port = transportSettings.httpPort,
        ) {
            routing {
                mcpStreamableHttp(
                    path = transportSettings.httpPath,
                ) {
                    val apiKey =
                        call.request.queryParameters["key"]
                            ?: call.request.headers["Authorization"]
                                ?.removePrefix("Bearer ")
                                ?.trim()

                    val user =
                        apiKey
                            ?.let { key ->
                                apiKeySettings.userForApiKey(key)
                            }

                    if (user == null) {
                        logger.error("Missing or invalid MCP API key: $apiKey")
                    }

                    requireNotNull(user) {
                        "Missing or invalid MCP API key."
                    }

                    serverFactory.create(
                        user = user,
                    )
                }
            }
        }.start(wait = true)
    }
}
