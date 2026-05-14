package com.felix.livinglink.core.delivery.mcp.server

import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.types.Implementation
import io.modelcontextprotocol.kotlin.sdk.types.ServerCapabilities
import org.koin.core.annotation.Single

@Single(binds = [McpServerFactory::class])
class LivingLinkMcpServerFactory(
    private val toolRegistrars: List<McpToolRegistrar>,
) : McpServerFactory {
    override fun create(user: McpRequestUser): Server =
        Server(
            serverInfo =
                Implementation(
                    name = "livinglink",
                    version = "0.1.0",
                ),
            options =
                ServerOptions(
                    capabilities =
                        ServerCapabilities(
                            tools = ServerCapabilities.Tools(listChanged = true),
                        ),
                ),
        ).also { server ->
            toolRegistrars.forEach { toolRegistrar ->
                toolRegistrar.register(
                    server = server,
                    user = user,
                )
            }
        }
}
