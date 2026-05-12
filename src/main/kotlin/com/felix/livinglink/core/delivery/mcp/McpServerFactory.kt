package com.felix.livinglink.core.delivery.mcp

import io.modelcontextprotocol.kotlin.sdk.server.Server

interface McpServerFactory {
    fun create(): Server
}
