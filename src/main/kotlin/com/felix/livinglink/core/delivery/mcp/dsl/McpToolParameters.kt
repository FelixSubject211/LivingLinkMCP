package com.felix.livinglink.core.delivery.mcp.dsl

abstract class McpToolParameters {
    private val mutableParameters: MutableList<McpToolParameter<*>> = mutableListOf()

    val parameters: List<McpToolParameter<*>>
        get() = mutableParameters

    fun requiredStringList(
        description: String,
    ): McpToolParameter<List<String>> =
        parameter(
            McpToolParameter.RequiredStringList(
                description = description,
            ),
        )

    fun optionalString(
        description: String,
        default: String? = null,
    ): McpToolParameter<String?> =
        parameter(
            McpToolParameter.OptionalString(
                description = description,
                default = default,
            ),
        )

    fun requiredString(
        description: String,
    ): McpToolParameter<String> =
        parameter(
            McpToolParameter.RequiredString(
                description = description,
            ),
        )

    fun optionalBoolean(
        description: String,
        default: Boolean? = null,
    ): McpToolParameter<Boolean?> =
        parameter(
            McpToolParameter.OptionalBoolean(
                description = description,
                default = default,
            ),
        )

    fun requiredInt(
        description: String,
        minimum: Int? = null,
        maximum: Int? = null,
    ): McpToolParameter<Int> =
        parameter(
            McpToolParameter.RequiredInt(
                description = description,
                minimum = minimum,
                maximum = maximum,
            ),
        )

    fun optionalInt(
        description: String,
        minimum: Int? = null,
        maximum: Int? = null,
        default: Int? = null,
    ): McpToolParameter<Int?> =
        parameter(
            McpToolParameter.OptionalInt(
                description = description,
                minimum = minimum,
                maximum = maximum,
                default = default,
            ),
        )

    fun optionalStringEnum(
        description: String,
        values: List<String>,
        default: String? = null,
    ): McpToolParameter<String?> =
        parameter(
            McpToolParameter.OptionalStringEnum(
                description = description,
                values = values,
                default = default,
            ),
        )

    fun requiredStringEnum(
        description: String,
        values: List<String>,
    ): McpToolParameter<String> =
        parameter(
            McpToolParameter.RequiredStringEnum(
                description = description,
                values = values,
            ),
        )

    fun <T> parameter(parameter: McpToolParameter<T>): McpToolParameter<T> {
        mutableParameters += parameter

        return parameter
    }
}
