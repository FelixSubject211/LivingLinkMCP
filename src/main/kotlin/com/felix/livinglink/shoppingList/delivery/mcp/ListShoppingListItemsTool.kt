package com.felix.livinglink.shoppingList.delivery.mcp

import com.felix.livinglink.core.delivery.mcp.dsl.McpToolDsl.tool
import com.felix.livinglink.core.delivery.mcp.dsl.McpToolParameters
import com.felix.livinglink.core.delivery.mcp.dsl.success
import com.felix.livinglink.core.delivery.mcp.server.McpToolRegistrar
import com.felix.livinglink.shoppingList.application.ListShoppingListItemsUseCase
import com.felix.livinglink.shoppingList.domain.ShoppingListItemQuery
import com.felix.livinglink.shoppingList.domain.ShoppingListItemSort
import io.modelcontextprotocol.kotlin.sdk.server.Server
import org.koin.core.annotation.Single

@Single(binds = [McpToolRegistrar::class])
class ListShoppingListItemsTool(
    private val listShoppingListItemsUseCase: ListShoppingListItemsUseCase,
) : McpToolRegistrar {
    override fun register(server: Server) {
        server.tool<Parameters>(
            name = "list_shopping_list_items",
            description = "Lists shopping list items with optional filtering, sorting and limit.",
        ) { parameters, arguments ->
            val items =
                listShoppingListItemsUseCase(
                    query =
                        ShoppingListItemQuery(
                            completed = arguments[parameters.completed],
                            limit = arguments[parameters.limit],
                            sort =
                                shoppingListItemSort(
                                    sortBy = arguments[parameters.sortBy],
                                    sortDirection = arguments[parameters.sortDirection],
                                ),
                        ),
                )

            success {
                ifEmpty(items, "No shopping list items found.") {
                    items.forEach { item ->
                        val status = if (item.completed) "done" else "open"

                        line(
                            "- [$status] ${item.name} " +
                                "(id: ${item.id}, createdAt: ${item.createdAt}, updatedAt: ${item.updatedAt})",
                        )
                    }
                }
            }
        }
    }

    private fun shoppingListItemSort(
        sortBy: String?,
        sortDirection: String?,
    ): ShoppingListItemSort =
        when ((sortBy ?: "createdAt") to (sortDirection ?: "desc")) {
            "createdAt" to "asc" -> ShoppingListItemSort.CreatedAtAscending
            "createdAt" to "desc" -> ShoppingListItemSort.CreatedAtDescending
            "updatedAt" to "asc" -> ShoppingListItemSort.UpdatedAtAscending
            "updatedAt" to "desc" -> ShoppingListItemSort.UpdatedAtDescending
            "name" to "asc" -> ShoppingListItemSort.NameAscending
            "name" to "desc" -> ShoppingListItemSort.NameDescending
            else -> error("Unsupported sort: sortBy=$sortBy, sortDirection=$sortDirection.")
        }

    class Parameters : McpToolParameters() {
        val completed by optionalBoolean(
            description = "Optional completion status filter.",
        )

        val limit by requiredInt(
            description = "Maximum number of items to return.",
            minimum = 1,
            maximum = 500,
        )

        val sortBy by optionalStringEnum(
            description = "Field to sort by.",
            values = listOf("createdAt", "updatedAt", "name"),
            default = "createdAt",
        )

        val sortDirection by optionalStringEnum(
            description = "Sort direction.",
            values = listOf("asc", "desc"),
            default = "desc",
        )
    }
}
