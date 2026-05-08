package com.felix.livinglink.mcp.shoppingList

import com.felix.livinglink.shoppingList.application.AddShoppingListItemUseCase
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

fun Server.registerAddShoppingListItemTool(
    addShoppingListItemUseCase: AddShoppingListItemUseCase
) {
    addTool(
        name = "add_shopping_list_item",
        description = "Adds an item to the shopping list.",
        inputSchema = ToolSchema(
            properties = buildJsonObject {
                put("name", buildJsonObject {
                    put("type", "string")
                    put("description", "Name of the item.")
                })
            },
            required = listOf("name")
        )
    ) { request ->
        val name = request.params.arguments?.get("name")?.jsonPrimitive?.content.orEmpty()
        val item = addShoppingListItemUseCase(name)

        CallToolResult(
            content = listOf(TextContent("Added '${item.name}' with id '${item.id}'."))
        )
    }
}