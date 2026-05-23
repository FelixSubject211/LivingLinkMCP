package com.felix.livinglink.core.delivery.mcp.dsl

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class McpToolArguments(
    private val values: Map<String, JsonElement>?,
    private val json: Json =
        Json {
            ignoreUnknownKeys = true
            classDiscriminator = McpToolSchemaBuilder.SEALED_DISCRIMINATOR
        },
) {
    operator fun <T> McpToolParameter<T>.invoke(): T =
        get(this)

    operator fun <T> get(parameter: McpToolParameter<T>): T =
        when (parameter) {
            is McpToolParameter.Required<*> -> required(parameter)
            is McpToolParameter.Optional<*> -> optional(parameter)
            is McpToolParameter.OptionalWithDefault<*> -> optionalWithDefault(parameter)
        }

    @Suppress("UNCHECKED_CAST")
    private fun <T> required(parameter: McpToolParameter.Required<*>): T {
        val value =
            requireNotNull(values?.get(parameter.name)) {
                "'${parameter.name}' is required."
            }

        val decoded =
            json.decodeFromJsonElement(
                deserializer = parameter.serializer,
                element = value,
            )

        parameter.validate(decoded)

        return decoded as T
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> optional(parameter: McpToolParameter.Optional<*>): T {
        val value = values?.get(parameter.name)

        val decoded =
            if (value == null) {
                parameter.default
            } else {
                json.decodeFromJsonElement(
                    deserializer = parameter.serializer,
                    element = value,
                )
            }

        parameter.validate(decoded)

        return decoded as T
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> optionalWithDefault(parameter: McpToolParameter.OptionalWithDefault<*>): T {
        val value = values?.get(parameter.name)

        val decoded =
            if (value == null) {
                parameter.default
            } else {
                json.decodeFromJsonElement(
                    deserializer = parameter.serializer,
                    element = value,
                )
            }

        parameter.validate(decoded)

        return decoded as T
    }
}
