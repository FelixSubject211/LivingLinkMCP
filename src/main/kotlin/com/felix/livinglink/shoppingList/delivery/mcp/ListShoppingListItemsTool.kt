package com.felix.livinglink.shoppingList.delivery.mcp

import com.felix.livinglink.core.delivery.mcp.dsl.McpToolDsl.tool
import com.felix.livinglink.core.delivery.mcp.dsl.success
import com.felix.livinglink.core.delivery.mcp.server.McpRequestUser
import com.felix.livinglink.core.delivery.mcp.server.McpToolRegistrar
import com.felix.livinglink.core.domain.UserLookup
import com.felix.livinglink.shoppingList.application.ListShoppingListItemsUseCase
import com.felix.livinglink.shoppingList.domain.ShoppingListItemQuery
import com.felix.livinglink.shoppingList.domain.ShoppingListItemSort
import io.modelcontextprotocol.kotlin.sdk.server.Server
import org.koin.core.annotation.Single

@Single(binds = [McpToolRegistrar::class])
class ListShoppingListItemsTool(
    private val listShoppingListItemsUseCase: ListShoppingListItemsUseCase,
    private val userLookup: UserLookup,
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
                optional<Boolean>(
                    name = "completed",
                    description = "Optional completion status filter.",
                )

            val limit =
                optionalInt(
                    name = "limit",
                    description = "Maximum number of items to return.",
                    minimum = 1,
                    maximum = 500,
                    default = 100,
                )

            val sort =
                optional<ShoppingListItemSort>(
                    name = "sort",
                    description = "Sort order.",
                    default = ShoppingListItemSort.CreatedAtDescending,
                )

            handle {
                val items =
                    listShoppingListItemsUseCase(
                        query =
                            ShoppingListItemQuery(
                                completed = completed(),
                                limit = limit(),
                                sort = sort(),
                            ),
                    )

                success {
                    ifEmpty(items, "No shopping list items found.") {
                        items.forEach { item ->
                            val createdByName =
                                userLookup.findById(item.createdByUserId)?.username
                                    ?: item.createdByUserId

                            val lastEvent = item.completionEvents.lastOrNull()
                            val status =
                                if (item.isCompleted && lastEvent != null) {
                                    val completedByName =
                                        userLookup.findById(lastEvent.byUserId)?.username
                                            ?: lastEvent.byUserId
                                    "done by $completedByName at ${lastEvent.at}"
                                } else {
                                    "open"
                                }

                            line(
                                "- [$status] ${item.name} " +
                                    "(id: ${item.id}, createdBy: $createdByName, createdAt: ${item.createdAt})",
                            )
                        }
                    }
                }
            }
        }
    }
}
