package com.felix.livinglink.core.delivery.mcp.dsl

import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

object McpToolDsl {
    inline fun <reified TParameters : McpToolParameters> Server.tool(
        name: String,
        description: String,
        noinline handle: suspend (parameters: TParameters, arguments: McpToolArguments) -> CallToolResult,
    ) {
        val parameters = TParameters::class.createInstance()

        parameters.initializeParameterNames()

        val definition =
            McpToolDefinition(
                name = name,
                description = description,
                parameters = parameters.parameters,
                handler = { arguments ->
                    handle(parameters, arguments)
                },
            )

        addTool(
            name = definition.name,
            description = definition.description,
            inputSchema = definition.schema(),
        ) { request ->
            runCatching {
                val arguments =
                    McpToolArguments(
                        values = request.params.arguments,
                    )

                definition.handler.invoke(arguments)
            }.getOrElse { exception ->
                toolError(exception.toToolErrorMessage())
            }
        }
    }
}

class McpToolDefinition(
    val name: String,
    val description: String,
    val parameters: List<McpToolParameter<*>>,
    val handler: suspend (McpToolArguments) -> CallToolResult,
) {
    fun schema(): ToolSchema =
        McpToolSchemaBuilder.build(parameters)
}

fun McpToolParameters.initializeParameterNames() {
    this::class.declaredMemberProperties.forEach { property ->
        property.isAccessible = true
        property.getter.call(this)
    }

    parameters.forEach { parameter ->
        require(parameter.hasName()) {
            "MCP tool parameter was registered but has no property name."
        }
    }
}
