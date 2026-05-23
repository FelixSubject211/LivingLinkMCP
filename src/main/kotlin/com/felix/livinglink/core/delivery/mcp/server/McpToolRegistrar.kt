package com.felix.livinglink.core.delivery.mcp.server

import com.felix.livinglink.core.config.McpRequestUser
import io.modelcontextprotocol.kotlin.sdk.server.Server

fun interface McpToolRegistrar {
    fun register(
        server: Server,
        user: McpRequestUser,
    )
}
