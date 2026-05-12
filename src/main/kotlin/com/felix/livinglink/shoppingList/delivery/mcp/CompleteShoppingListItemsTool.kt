package com.felix.livinglink.shoppingList.delivery.mcp

import com.felix.livinglink.core.delivery.mcp.dsl.McpToolDsl.tool
import com.felix.livinglink.core.delivery.mcp.dsl.McpToolParameters
import com.felix.livinglink.core.delivery.mcp.dsl.success
import com.felix.livinglink.core.delivery.mcp.server.McpToolRegistrar
import com.felix.livinglink.shoppingList.application.CompleteShoppingListItemsUseCase
import io.modelcontextprotocol.kotlin.sdk.server.Server
import org.koin.core.annotation.Single

@Single(binds = [McpToolRegistrar::class])
class CompleteShoppingListItemsTool(
    private val completeShoppingListItemsUseCase: CompleteShoppingListItemsUseCase,
) : McpToolRegistrar {
    override fun register(server: Server) {
        server.tool<Parameters>(
            name = "complete_shopping_list_items",
            description = "Marks one or more shopping list items as completed.",
        ) { parameters, arguments ->
            val result =
                completeShoppingListItemsUseCase(
                    ids = arguments[parameters.ids],
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

    class Parameters : McpToolParameters() {
        val ids by requiredStringList(
            description = "IDs of the items.",
        )
    }
}
