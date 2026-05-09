package com.felix.livinglink.infrastructure.mcp

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
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
