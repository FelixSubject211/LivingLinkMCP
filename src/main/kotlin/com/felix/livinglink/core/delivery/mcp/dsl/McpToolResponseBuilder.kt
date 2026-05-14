package com.felix.livinglink.core.delivery.mcp.dsl

import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent

class McpToolResponseBuilder {
    private val lines: MutableList<String> = mutableListOf()

    fun line(value: String) {
        lines += value
    }

    suspend fun <T> ifEmpty(
        values: List<T>,
        emptyMessage: String,
        block: suspend McpToolResponseBuilder.() -> Unit,
    ) {
        if (values.isEmpty()) {
            line(emptyMessage)
        } else {
            block()
        }
    }

    fun build(): CallToolResult =
        CallToolResult(
            content = listOf(TextContent(lines.joinToString(separator = "\n"))),
        )
}

suspend fun success(
    block: suspend McpToolResponseBuilder.() -> Unit,
): CallToolResult =
    McpToolResponseBuilder()
        .apply { block() }
        .build()

fun success(message: String): CallToolResult =
    CallToolResult(
        content = listOf(TextContent(message)),
    )
