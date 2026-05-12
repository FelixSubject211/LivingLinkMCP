package com.felix.livinglink.core.delivery.mcp.dsl

import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent

class McpToolResponseBuilder {
    private val lines: MutableList<String> = mutableListOf()

    fun line(value: String) {
        lines += value
    }

    fun <T> ifEmpty(
        values: List<T>,
        emptyMessage: String,
        block: McpToolResponseBuilder.() -> Unit,
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

fun success(
    block: McpToolResponseBuilder.() -> Unit,
): CallToolResult =
    McpToolResponseBuilder()
        .apply(block)
        .build()

fun success(message: String): CallToolResult =
    CallToolResult(
        content = listOf(TextContent(message)),
    )
