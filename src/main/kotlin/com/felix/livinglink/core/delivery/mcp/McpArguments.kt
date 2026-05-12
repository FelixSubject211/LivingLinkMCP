package com.felix.livinglink.core.delivery.mcp

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

fun Map<String, JsonElement>?.stringListArgument(name: String): List<String> {
    val value = this?.get(name) ?: return emptyList()

    if (value is JsonArray) {
        return value.map { element ->
            element.jsonPrimitive.content
        }
    }

    return listOf(value.jsonPrimitive.content)
}

fun Map<String, JsonElement>?.optionalStringArgument(name: String): String? =
    this
        ?.get(name)
        ?.jsonPrimitive
        ?.content
        ?.trim()
        ?.takeIf { value ->
            value.isNotBlank()
        }

fun Map<String, JsonElement>?.optionalBooleanArgument(name: String): Boolean? =
    this
        ?.get(name)
        ?.jsonPrimitive
        ?.booleanOrNull

fun Map<String, JsonElement>?.optionalIntArgument(name: String): Int? =
    this
        ?.get(name)
        ?.jsonPrimitive
        ?.intOrNull
