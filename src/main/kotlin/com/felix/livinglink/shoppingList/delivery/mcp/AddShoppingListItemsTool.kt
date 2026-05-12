package com.felix.livinglink.shoppingList.delivery.mcp

import com.felix.livinglink.core.delivery.mcp.dsl.McpToolDsl.tool
import com.felix.livinglink.core.delivery.mcp.dsl.McpToolParameters
import com.felix.livinglink.core.delivery.mcp.dsl.success
import com.felix.livinglink.core.delivery.mcp.server.McpToolRegistrar
import com.felix.livinglink.shoppingList.application.AddShoppingListItemsUseCase
import io.modelcontextprotocol.kotlin.sdk.server.Server
import org.koin.core.annotation.Single

@Single(binds = [McpToolRegistrar::class])
class AddShoppingListItemsTool(
    private val addShoppingListItemsUseCase: AddShoppingListItemsUseCase,
) : McpToolRegistrar {
    override fun register(server: Server) {
        server.tool<Parameters>(
            name = "add_shopping_list_items",
            description = "Adds one or more items to the shopping list.",
        ) { parameters, arguments ->
            val items =
                addShoppingListItemsUseCase(
                    names = arguments[parameters.names],
                )

            success {
                items.forEach { item ->
                    line("- Added '${item.name}' with id '${item.id}'.")
                }
            }
        }
    }

    class Parameters : McpToolParameters() {
        val names by requiredStringList(
            description = "Names of the items.",
        )
    }
}
