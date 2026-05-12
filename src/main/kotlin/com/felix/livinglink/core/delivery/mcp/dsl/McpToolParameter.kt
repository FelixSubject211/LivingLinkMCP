package com.felix.livinglink.core.delivery.mcp.dsl

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

sealed class McpToolParameter<T>(
    val description: String,
    val required: Boolean,
) : ReadOnlyProperty<Any?, McpToolParameter<T>> {
    lateinit var name: String
        private set

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): McpToolParameter<T> {
        if (!::name.isInitialized) {
            name = property.name
        }

        return this
    }

    fun hasName(): Boolean =
        ::name.isInitialized

    class RequiredStringList(
        description: String,
    ) : McpToolParameter<List<String>>(
            description = description,
            required = true,
        )

    class RequiredString(
        description: String,
    ) : McpToolParameter<String>(
            description = description,
            required = true,
        )

    class OptionalString(
        description: String,
        val default: String?,
    ) : McpToolParameter<String?>(
            description = description,
            required = false,
        )

    class OptionalBoolean(
        description: String,
        val default: Boolean?,
    ) : McpToolParameter<Boolean?>(
            description = description,
            required = false,
        )

    class RequiredInt(
        description: String,
        val minimum: Int?,
        val maximum: Int?,
    ) : McpToolParameter<Int>(
            description = description,
            required = true,
        )

    class OptionalInt(
        description: String,
        val minimum: Int?,
        val maximum: Int?,
        val default: Int?,
    ) : McpToolParameter<Int?>(
            description = description,
            required = false,
        )

    class RequiredStringEnum(
        description: String,
        val values: List<String>,
    ) : McpToolParameter<String>(
            description = description,
            required = true,
        )

    class OptionalStringEnum(
        description: String,
        val values: List<String>,
        val default: String?,
    ) : McpToolParameter<String?>(
            description = description,
            required = false,
        )
}
