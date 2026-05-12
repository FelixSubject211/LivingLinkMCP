package com.felix.livinglink.shoppingList.delivery.mcp

import com.felix.livinglink.core.delivery.mcp.dsl.McpToolDsl.tool
import com.felix.livinglink.core.delivery.mcp.dsl.success
import com.felix.livinglink.core.delivery.mcp.server.McpToolRegistrar
import com.felix.livinglink.shoppingList.application.AddShoppingListItemsUseCase
import org.koin.core.annotation.Single

@Single(binds = [McpToolRegistrar::class])
fun addShoppingListItemsTool(
    addShoppingListItemsUseCase: AddShoppingListItemsUseCase,
): McpToolRegistrar =
    McpToolRegistrar { server ->
        server.tool(
            name = "add_shopping_list_items",
            description = "Adds one or more items to the shopping list.",
        ) {
            val names =
                requiredStringList(
                    name = "names",
                    description = "Names of the items.",
                )

            handle {
                val items =
                    addShoppingListItemsUseCase(
                        names = names(),
                    )

                success {
                    items.forEach { item ->
                        line("- Added '${item.name}' with id '${item.id}'.")
                    }
                }
            }
        }
    }
