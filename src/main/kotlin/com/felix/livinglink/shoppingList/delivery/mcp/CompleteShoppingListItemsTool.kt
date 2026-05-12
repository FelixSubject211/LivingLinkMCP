package com.felix.livinglink.shoppingList.delivery.mcp

import com.felix.livinglink.core.delivery.mcp.dsl.McpToolDsl.tool
import com.felix.livinglink.core.delivery.mcp.dsl.success
import com.felix.livinglink.core.delivery.mcp.server.McpToolRegistrar
import com.felix.livinglink.shoppingList.application.CompleteShoppingListItemsUseCase
import org.koin.core.annotation.Single

@Single(binds = [McpToolRegistrar::class])
fun completeShoppingListItemsTool(
    completeShoppingListItemsUseCase: CompleteShoppingListItemsUseCase,
): McpToolRegistrar =
    McpToolRegistrar { server ->
        server.tool(
            name = "complete_shopping_list_items",
            description = "Marks one or more shopping list items as completed.",
        ) {
            val ids =
                requiredStringList(
                    name = "ids",
                    description = "IDs of the items.",
                )

            handle {
                val result =
                    completeShoppingListItemsUseCase(
                        ids = ids(),
                    )

                success {
                    result.completedItems.forEach { item ->
                        line("- Completed '${item.name}'.")
                    }

                    result.alreadyCompletedItems.forEach { item ->
                        line("- '${item.name}' was already completed.")
                    }

                    result.missingIds.forEach { id ->
                        line("- Item with id '$id' was not found.")
                    }
                }
            }
        }
    }
