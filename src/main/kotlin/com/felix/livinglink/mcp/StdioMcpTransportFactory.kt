package com.felix.livinglink.mcp

import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport

interface StdioMcpTransportFactory {
    fun create(): StdioServerTransport
}
