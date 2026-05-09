package com.felix.livinglink.infrastructure.mcp

import io.modelcontextprotocol.kotlin.sdk.server.Server

interface McpToolRegistrar {
    fun register(server: Server)
}
