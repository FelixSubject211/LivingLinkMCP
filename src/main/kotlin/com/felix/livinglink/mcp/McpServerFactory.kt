package com.felix.livinglink.mcp

import io.modelcontextprotocol.kotlin.sdk.server.Server

interface McpServerFactory {
    fun create(): Server
}
