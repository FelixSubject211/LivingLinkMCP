package com.felix.livinglink.core.delivery.mcp

import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport

interface StdioMcpTransportFactory {
    fun create(): StdioServerTransport
}
