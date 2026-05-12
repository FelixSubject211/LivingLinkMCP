package com.felix.livinglink.core.delivery.mcp.dsl

import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult

class McpToolBuilder {
    private val mutableParameters: MutableList<McpToolParameter<*>> = mutableListOf()

    private var mutableHandler: (suspend McpToolArguments.() -> CallToolResult)? = null

    val parameters: List<McpToolParameter<*>>
        get() = mutableParameters

    val handler: suspend McpToolArguments.() -> CallToolResult
        get() =
            requireNotNull(mutableHandler) {
                "MCP tool handler is required. Use handle { ... } inside the tool block."
            }

    fun requiredStringList(
        name: String,
        description: String,
    ): McpToolParameter<List<String>> =
        parameter(
            McpToolParameter.RequiredStringList(
                name = name,
                description = description,
            ),
        )

    fun optionalString(
        name: String,
        description: String,
        default: String? = null,
    ): McpToolParameter<String?> =
        parameter(
            McpToolParameter.OptionalString(
                name = name,
                description = description,
                default = default,
            ),
        )

    fun requiredString(
        name: String,
        description: String,
    ): McpToolParameter<String> =
        parameter(
            McpToolParameter.RequiredString(
                name = name,
                description = description,
            ),
        )

    fun optionalBoolean(
        name: String,
        description: String,
        default: Boolean? = null,
    ): McpToolParameter<Boolean?> =
        parameter(
            McpToolParameter.OptionalBoolean(
                name = name,
                description = description,
                default = default,
            ),
        )

    fun requiredInt(
        name: String,
        description: String,
        minimum: Int? = null,
        maximum: Int? = null,
    ): McpToolParameter<Int> =
        parameter(
            McpToolParameter.RequiredInt(
                name = name,
                description = description,
                minimum = minimum,
                maximum = maximum,
            ),
        )

    fun optionalInt(
        name: String,
        description: String,
        minimum: Int? = null,
        maximum: Int? = null,
        default: Int? = null,
    ): McpToolParameter<Int?> =
        parameter(
            McpToolParameter.OptionalInt(
                name = name,
                description = description,
                minimum = minimum,
                maximum = maximum,
                default = default,
            ),
        )

    fun optionalStringEnum(
        name: String,
        description: String,
        values: List<String>,
        default: String? = null,
    ): McpToolParameter<String?> =
        parameter(
            McpToolParameter.OptionalStringEnum(
                name = name,
                description = description,
                values = values,
                default = default,
            ),
        )

    fun requiredStringEnum(
        name: String,
        description: String,
        values: List<String>,
    ): McpToolParameter<String> =
        parameter(
            McpToolParameter.RequiredStringEnum(
                name = name,
                description = description,
                values = values,
            ),
        )

    fun handle(handler: suspend McpToolArguments.() -> CallToolResult) {
        mutableHandler = handler
    }

    private fun <T> parameter(parameter: McpToolParameter<T>): McpToolParameter<T> {
        mutableParameters += parameter

        return parameter
    }
}
