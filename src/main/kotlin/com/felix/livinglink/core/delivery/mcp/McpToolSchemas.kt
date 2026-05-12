package com.felix.livinglink.core.delivery.mcp

import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

fun stringArrayPropertySchema(
    name: String,
    description: String,
): ToolSchema =
    ToolSchema(
        properties =
            buildJsonObject {
                putJsonObject(name) {
                    put("type", "array")
                    put("description", description)
                    putJsonObject("items") {
                        put("type", "string")
                    }
                }
            },
        required = listOf(name),
    )
