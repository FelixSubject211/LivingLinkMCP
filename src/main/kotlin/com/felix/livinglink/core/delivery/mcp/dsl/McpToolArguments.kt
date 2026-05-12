package com.felix.livinglink.core.delivery.mcp.dsl

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

class McpToolArguments(
    private val values: Map<String, JsonElement>?,
) {
    operator fun <T> McpToolParameter<T>.invoke(): T =
        get(this)

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(parameter: McpToolParameter<T>): T =
        when (parameter) {
            is McpToolParameter.RequiredStringList -> requiredStringList(parameter)
            is McpToolParameter.RequiredString -> requiredString(parameter)
            is McpToolParameter.OptionalString -> optionalString(parameter)
            is McpToolParameter.OptionalBoolean -> optionalBoolean(parameter)
            is McpToolParameter.RequiredInt -> requiredInt(parameter)
            is McpToolParameter.OptionalInt -> optionalInt(parameter)
            is McpToolParameter.RequiredStringEnum -> requiredStringEnum(parameter)
            is McpToolParameter.OptionalStringEnum -> optionalStringEnum(parameter)
        } as T

    private fun requiredStringList(
        parameter: McpToolParameter.RequiredStringList,
    ): List<String> {
        val value =
            requireNotNull(values?.get(parameter.name)) {
                "'${parameter.name}' is required."
            }

        val rawValues =
            if (value is JsonArray) {
                value.map { element ->
                    element.jsonPrimitive.content
                }
            } else {
                listOf(value.jsonPrimitive.content)
            }

        val cleanedValues =
            rawValues
                .map { item ->
                    item.trim()
                }.filter { item ->
                    item.isNotBlank()
                }.distinct()

        require(cleanedValues.isNotEmpty()) {
            "'${parameter.name}' must contain at least one value."
        }

        return cleanedValues
    }

    private fun requiredString(
        parameter: McpToolParameter.RequiredString,
    ): String =
        requireNotNull(optionalStringValue(parameter.name)) {
            "'${parameter.name}' is required."
        }

    private fun optionalString(
        parameter: McpToolParameter.OptionalString,
    ): String? =
        optionalStringValue(parameter.name) ?: parameter.default

    private fun optionalBoolean(
        parameter: McpToolParameter.OptionalBoolean,
    ): Boolean? =
        values
            ?.get(parameter.name)
            ?.jsonPrimitive
            ?.booleanOrNull
            ?: parameter.default

    private fun requiredInt(
        parameter: McpToolParameter.RequiredInt,
    ): Int {
        val value =
            requireNotNull(
                values
                    ?.get(parameter.name)
                    ?.jsonPrimitive
                    ?.intOrNull,
            ) {
                "'${parameter.name}' is required."
            }

        validateRange(
            name = parameter.name,
            value = value,
            minimum = parameter.minimum,
            maximum = parameter.maximum,
        )

        return value
    }

    private fun optionalInt(
        parameter: McpToolParameter.OptionalInt,
    ): Int? {
        val value =
            values
                ?.get(parameter.name)
                ?.jsonPrimitive
                ?.intOrNull
                ?: parameter.default
                ?: return null

        validateRange(
            name = parameter.name,
            value = value,
            minimum = parameter.minimum,
            maximum = parameter.maximum,
        )

        return value
    }

    private fun requiredStringEnum(
        parameter: McpToolParameter.RequiredStringEnum,
    ): String {
        val value =
            requireNotNull(optionalStringValue(parameter.name)) {
                "'${parameter.name}' is required."
            }

        validateEnum(
            name = parameter.name,
            value = value,
            values = parameter.values,
        )

        return value
    }

    private fun optionalStringEnum(
        parameter: McpToolParameter.OptionalStringEnum,
    ): String? {
        val value =
            optionalStringValue(parameter.name) ?: parameter.default ?: return null

        validateEnum(
            name = parameter.name,
            value = value,
            values = parameter.values,
        )

        return value
    }

    private fun optionalStringValue(name: String): String? =
        values
            ?.get(name)
            ?.jsonPrimitive
            ?.content
            ?.trim()
            ?.takeIf { value ->
                value.isNotBlank()
            }

    private fun validateRange(
        name: String,
        value: Int,
        minimum: Int?,
        maximum: Int?,
    ) {
        if (minimum != null) {
            require(value >= minimum) {
                "'$name' must be greater than or equal to $minimum."
            }
        }

        if (maximum != null) {
            require(value <= maximum) {
                "'$name' must be less than or equal to $maximum."
            }
        }
    }

    private fun validateEnum(
        name: String,
        value: String,
        values: List<String>,
    ) {
        require(value in values) {
            "'$name' must be one of: ${values.joinToString()}."
        }
    }
}
