package com.felix.livinglink.server.core.delivery.mcp.server

import com.felix.livinglink.server.core.config.McpRequestUser
import io.modelcontextprotocol.kotlin.sdk.server.Server

interface McpServerFactory {
    fun create(user: McpRequestUser): Server
}
