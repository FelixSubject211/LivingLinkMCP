package com.felix.livinglink.shoppingList.mcp

import com.felix.livinglink.mcp.McpToolRegistrar
import com.felix.livinglink.shoppingList.application.AddShoppingListItemUseCase
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.koin.core.annotation.Single

@Single(binds = [McpToolRegistrar::class])
class AddShoppingListItemTool(
    private val addShoppingListItemUseCase: AddShoppingListItemUseCase,
) : McpToolRegistrar {
    override fun register(server: Server) {
        server.addTool(
            name = "add_shopping_list_item",
            description = "Adds an item to the shopping list.",
            inputSchema =
                ToolSchema(
                    properties =
                        buildJsonObject {
                            put(
                                "name",
                                buildJsonObject {
                                    put("type", "string")
                                    put("description", "Name of the item.")
                                },
                            )
                        },
                    required = listOf("name"),
                ),
        ) { request ->
            val name =
                request.params.arguments
                    ?.get("name")
                    ?.jsonPrimitive
                    ?.content
                    .orEmpty()

            val item =
                runCatching {
                    addShoppingListItemUseCase(name)
                }.getOrElse { exception ->
                    return@addTool CallToolResult(
                        content =
                            listOf(
                                TextContent(
                                    exception.message
                                        ?: "Could not add shopping list item.",
                                ),
                            ),
                        isError = true,
                    )
                }

            CallToolResult(
                content = listOf(TextContent("Added '${item.name}' with id '${item.id}'.")),
            )
        }
    }
}
