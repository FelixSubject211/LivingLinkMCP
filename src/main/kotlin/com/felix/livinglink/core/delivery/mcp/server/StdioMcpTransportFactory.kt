package com.felix.livinglink.core.delivery.mcp.server

import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport

interface StdioMcpTransportFactory {
    fun create(): StdioServerTransport
}
