package com.felix.livinglink.infrastructure.mcp

import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent

fun toolSuccess(message: String): CallToolResult =
    CallToolResult(
        content = listOf(TextContent(message)),
    )

fun toolError(message: String): CallToolResult =
    CallToolResult(
        content = listOf(TextContent(message)),
        isError = true,
    )

inline fun catchingToolErrors(block: () -> CallToolResult): CallToolResult =
    runCatching {
        block()
    }.getOrElse { exception ->
        toolError(exception.message ?: "Tool execution failed.")
    }
