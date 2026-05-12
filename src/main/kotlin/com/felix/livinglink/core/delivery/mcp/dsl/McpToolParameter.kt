package com.felix.livinglink.core.delivery.mcp.dsl

sealed class McpToolParameter<T>(
    val name: String,
    val description: String,
    val required: Boolean,
) {
    class RequiredStringList(
        name: String,
        description: String,
    ) : McpToolParameter<List<String>>(
            name = name,
            description = description,
            required = true,
        )

    class RequiredString(
        name: String,
        description: String,
    ) : McpToolParameter<String>(
            name = name,
            description = description,
            required = true,
        )

    class OptionalString(
        name: String,
        description: String,
        val default: String?,
    ) : McpToolParameter<String?>(
            name = name,
            description = description,
            required = false,
        )

    class OptionalBoolean(
        name: String,
        description: String,
        val default: Boolean?,
    ) : McpToolParameter<Boolean?>(
            name = name,
            description = description,
            required = false,
        )

    class RequiredInt(
        name: String,
        description: String,
        val minimum: Int?,
        val maximum: Int?,
    ) : McpToolParameter<Int>(
            name = name,
            description = description,
            required = true,
        )

    class OptionalInt(
        name: String,
        description: String,
        val minimum: Int?,
        val maximum: Int?,
        val default: Int?,
    ) : McpToolParameter<Int?>(
            name = name,
            description = description,
            required = false,
        )

    class RequiredStringEnum(
        name: String,
        description: String,
        val values: List<String>,
    ) : McpToolParameter<String>(
            name = name,
            description = description,
            required = true,
        )

    class OptionalStringEnum(
        name: String,
        description: String,
        val values: List<String>,
        val default: String?,
    ) : McpToolParameter<String?>(
            name = name,
            description = description,
            required = false,
        )
}
