package com.felix.livinglink.core.delivery.mcp.dsl

import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent

fun toolError(message: String): CallToolResult =
    CallToolResult(
        content = listOf(TextContent(message)),
        isError = true,
    )
