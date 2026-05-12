package com.felix.livinglink.core.delivery.mcp.dsl

import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

object McpToolSchemaBuilder {
    fun build(parameters: List<McpToolParameter<*>>): ToolSchema =
        ToolSchema(
            properties =
                buildJsonObject {
                    parameters.forEach { parameter ->
                        putJsonObject(parameter.name) {
                            put("description", parameter.description)

                            when (parameter) {
                                is McpToolParameter.RequiredStringList -> {
                                    put("type", "array")
                                    putJsonObject("items") {
                                        put("type", "string")
                                    }
                                }

                                is McpToolParameter.RequiredString -> {
                                    put("type", "string")
                                }

                                is McpToolParameter.OptionalString -> {
                                    put("type", "string")
                                }

                                is McpToolParameter.OptionalBoolean -> {
                                    put("type", "boolean")
                                }

                                is McpToolParameter.RequiredInt -> {
                                    put("type", "integer")
                                    parameter.minimum?.let { minimum ->
                                        put("minimum", minimum)
                                    }
                                    parameter.maximum?.let { maximum ->
                                        put("maximum", maximum)
                                    }
                                }

                                is McpToolParameter.OptionalInt -> {
                                    put("type", "integer")
                                    parameter.minimum?.let { minimum ->
                                        put("minimum", minimum)
                                    }
                                    parameter.maximum?.let { maximum ->
                                        put("maximum", maximum)
                                    }
                                }

                                is McpToolParameter.RequiredStringEnum -> {
                                    put("type", "string")
                                    putJsonArray("enum") {
                                        parameter.values.forEach { value ->
                                            add(value)
                                        }
                                    }
                                }

                                is McpToolParameter.OptionalStringEnum -> {
                                    put("type", "string")
                                    putJsonArray("enum") {
                                        parameter.values.forEach { value ->
                                            add(value)
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
            required =
                parameters
                    .filter { parameter ->
                        parameter.required
                    }.map { parameter ->
                        parameter.name
                    },
        )
}
