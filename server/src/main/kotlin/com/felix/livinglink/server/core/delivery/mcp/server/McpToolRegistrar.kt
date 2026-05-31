package com.felix.livinglink.server.core.delivery.mcp.server

import com.felix.livinglink.server.core.config.McpRequestUser
import io.modelcontextprotocol.kotlin.sdk.server.Server

fun interface McpToolRegistrar {
    fun register(
        server: Server,
        user: McpRequestUser,
    )
}
