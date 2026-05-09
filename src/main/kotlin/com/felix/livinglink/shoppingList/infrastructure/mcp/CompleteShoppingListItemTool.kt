package com.felix.livinglink.shoppingList.infrastructure.mcp

import com.felix.livinglink.infrastructure.mcp.McpToolRegistrar
import com.felix.livinglink.shoppingList.application.CompleteShoppingListItemUseCase
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.koin.core.annotation.Single

@Single(binds = [McpToolRegistrar::class])
class CompleteShoppingListItemTool(
    private val completeShoppingListItemUseCase: CompleteShoppingListItemUseCase,
) : McpToolRegistrar {
    override fun register(server: Server) {
        server.addTool(
            name = "complete_shopping_list_item",
            description = "Marks a shopping list item as completed.",
            inputSchema =
                ToolSchema(
                    properties =
                        buildJsonObject {
                            put(
                                "id",
                                buildJsonObject {
                                    put("type", "string")
                                    put("description", "ID of the item.")
                                },
                            )
                        },
                    required = listOf("id"),
                ),
        ) { request ->
            val id =
                request.params.arguments
                    ?.get("id")
                    ?.jsonPrimitive
                    ?.content
                    .orEmpty()

            val item = completeShoppingListItemUseCase(id)

            if (item == null) {
                CallToolResult(
                    content = listOf(TextContent("Item with id '$id' not found.")),
                    isError = true,
                )
            } else {
                CallToolResult(
                    content = listOf(TextContent("Completed '${item.name}'.")),
                )
            }
        }
    }
}
