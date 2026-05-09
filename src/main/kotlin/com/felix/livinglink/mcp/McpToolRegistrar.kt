package com.felix.livinglink.mcp

import io.modelcontextprotocol.kotlin.sdk.server.Server

interface McpToolRegistrar {
    fun register(server: Server)
}
