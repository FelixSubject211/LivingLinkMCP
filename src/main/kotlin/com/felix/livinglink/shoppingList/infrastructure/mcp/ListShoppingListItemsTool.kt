package com.felix.livinglink.shoppingList.infrastructure.mcp

import com.felix.livinglink.infrastructure.mcp.McpToolRegistrar
import com.felix.livinglink.shoppingList.application.ListShoppingListItemsUseCase
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import org.koin.core.annotation.Single

@Single(binds = [McpToolRegistrar::class])
class ListShoppingListItemsTool(
    private val listShoppingListItemsUseCase: ListShoppingListItemsUseCase,
) : McpToolRegistrar {
    override fun register(server: Server) {
        server.addTool(
            name = "list_shopping_list_items",
            description = "Lists all shopping list items.",
            inputSchema = ToolSchema(),
        ) {
            val items = listShoppingListItemsUseCase()

            val text =
                if (items.isEmpty()) {
                    "Shopping list is empty."
                } else {
                    items.joinToString(separator = "\n") { item ->
                        val status = if (item.completed) "done" else "open"
                        "- [$status] ${item.name} (id: ${item.id})"
                    }
                }

            CallToolResult(content = listOf(TextContent(text)))
        }
    }
}
