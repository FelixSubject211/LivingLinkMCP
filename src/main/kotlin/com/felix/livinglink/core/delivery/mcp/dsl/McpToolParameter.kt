package com.felix.livinglink.core.delivery.mcp.dsl

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.json.JsonElement

sealed class McpToolParameter<T>(
    val name: String,
    val description: String,
    val required: Boolean,
    val serializer: KSerializer<T>,
    val schema: JsonElement,
) {
    abstract fun validate(value: Any?)

    class Required<T : Any>(
        name: String,
        description: String,
        serializer: KSerializer<T>,
        schema: JsonElement,
        private val validator: (T) -> Unit = {},
    ) : McpToolParameter<T>(
            name = name,
            description = description,
            required = true,
            serializer = serializer,
            schema = schema,
        ) {
        @Suppress("UNCHECKED_CAST")
        override fun validate(value: Any?) {
            validator(value as T)
        }
    }

    class Optional<T : Any>(
        name: String,
        description: String,
        serializer: KSerializer<T>,
        schema: JsonElement,
        val default: T?,
        private val validator: (T) -> Unit = {},
    ) : McpToolParameter<T?>(
            name = name,
            description = description,
            required = false,
            serializer = serializer.nullable,
            schema = schema,
        ) {
        @Suppress("UNCHECKED_CAST")
        override fun validate(value: Any?) {
            value?.let { presentValue ->
                validator(presentValue as T)
            }
        }
    }

    class OptionalWithDefault<T : Any>(
        name: String,
        description: String,
        serializer: KSerializer<T>,
        schema: JsonElement,
        val default: T,
        private val validator: (T) -> Unit = {},
    ) : McpToolParameter<T>(
            name = name,
            description = description,
            required = false,
            serializer = serializer,
            schema = schema,
        ) {
        @Suppress("UNCHECKED_CAST")
        override fun validate(value: Any?) {
            validator(value as T)
        }
    }
}
