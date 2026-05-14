package com.felix.livinglink.shoppingList.delivery.mcp

import com.felix.livinglink.core.delivery.mcp.dsl.McpToolDsl.tool
import com.felix.livinglink.core.delivery.mcp.dsl.success
import com.felix.livinglink.core.delivery.mcp.server.McpRequestUser
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
    override fun register(
        server: Server,
        user: McpRequestUser,
    ) {
        server.tool(
            name = "list_shopping_list_items",
            description = "Lists shopping list items with optional filtering, sorting and limit.",
        ) {
            val completed =
                optionalBoolean(
                    name = "completed",
                    description = "Optional completion status filter.",
                )

            val limit =
                requiredInt(
                    name = "limit",
                    description = "Maximum number of items to return.",
                    minimum = 1,
                    maximum = 500,
                )

            val sortBy =
                optionalStringEnum(
                    name = "sortBy",
                    description = "Field to sort by.",
                    values = listOf("createdAt", "updatedAt", "name"),
                    default = "createdAt",
                )

            val sortDirection =
                optionalStringEnum(
                    name = "sortDirection",
                    description = "Sort direction.",
                    values = listOf("asc", "desc"),
                    default = "desc",
                )

            handle {
                val items =
                    listShoppingListItemsUseCase(
                        query =
                            ShoppingListItemQuery(
                                completed = completed(),
                                limit = limit(),
                                sort =
                                    shoppingListItemSort(
                                        sortBy = sortBy(),
                                        sortDirection = sortDirection(),
                                    ),
                            ),
                    )

                success {
                    line("userId=${user.id}")
                    line("username=${user.username}")

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
