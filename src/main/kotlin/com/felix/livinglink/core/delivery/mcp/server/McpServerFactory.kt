package com.felix.livinglink.core.delivery.mcp.server

import com.felix.livinglink.core.config.McpRequestUser
import io.modelcontextprotocol.kotlin.sdk.server.Server

interface McpServerFactory {
    fun create(user: McpRequestUser): Server
}
