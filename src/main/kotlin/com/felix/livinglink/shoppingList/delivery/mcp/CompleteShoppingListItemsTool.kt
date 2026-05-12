package com.felix.livinglink.shoppingList.delivery.mcp

import com.felix.livinglink.core.delivery.mcp.McpToolRegistrar
import com.felix.livinglink.core.delivery.mcp.catchingToolErrors
import com.felix.livinglink.core.delivery.mcp.stringArrayPropertySchema
import com.felix.livinglink.core.delivery.mcp.stringListArgument
import com.felix.livinglink.core.delivery.mcp.toolSuccess
import com.felix.livinglink.shoppingList.application.CompleteShoppingListItemsUseCase
import io.modelcontextprotocol.kotlin.sdk.server.Server
import org.koin.core.annotation.Single

@Single(binds = [McpToolRegistrar::class])
class CompleteShoppingListItemsTool(
    private val completeShoppingListItemsUseCase: CompleteShoppingListItemsUseCase,
) : McpToolRegistrar {
    override fun register(server: Server) {
        server.addTool(
            name = "complete_shopping_list_items",
            description = "Marks one or more shopping list items as completed.",
            inputSchema =
                stringArrayPropertySchema(
                    name = "ids",
                    description = "IDs of the items.",
                ),
        ) { request ->
            catchingToolErrors {
                val result =
                    completeShoppingListItemsUseCase(
                        request.params.arguments.stringListArgument("ids"),
                    )

                val text =
                    buildList {
                        result.completedItems.forEach { item ->
                            add("- Completed '${item.name}'.")
                        }

                        result.alreadyCompletedItems.forEach { item ->
                            add("- '${item.name}' was already completed.")
                        }

                        result.missingIds.forEach { id ->
                            add("- Item with id '$id' was not found.")
                        }
                    }.joinToString(separator = "\n")

                toolSuccess(text)
            }
        }
    }
}
