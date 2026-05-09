package com.felix.livinglink.infrastructure.mcp

import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport

interface StdioMcpTransportFactory {
    fun create(): StdioServerTransport
}
