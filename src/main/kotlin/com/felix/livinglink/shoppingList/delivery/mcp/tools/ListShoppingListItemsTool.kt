package com.felix.livinglink.shoppingList.delivery.mcp.tools

import com.felix.livinglink.core.delivery.mcp.dsl.McpToolDsl.tool
import com.felix.livinglink.core.delivery.mcp.dsl.resolveUsers
import com.felix.livinglink.core.delivery.mcp.dsl.success
import com.felix.livinglink.core.delivery.mcp.dsl.toMcpString
import com.felix.livinglink.core.delivery.mcp.server.McpRequestUser
import com.felix.livinglink.core.delivery.mcp.server.McpToolRegistrar
import com.felix.livinglink.core.infrastructure.system.TimezoneSettings
import com.felix.livinglink.shoppingList.application.ListShoppingListItemsUseCase
import com.felix.livinglink.shoppingList.delivery.mcp.dto.ShoppingListItemSortMcpDto
import com.felix.livinglink.shoppingList.domain.ShoppingListItemQuery
import com.felix.livinglink.user.domain.UserLookup
import io.modelcontextprotocol.kotlin.sdk.server.Server
import org.koin.core.annotation.Single

@Single(binds = [McpToolRegistrar::class])
class ListShoppingListItemsTool(
    private val listShoppingListItemsUseCase: ListShoppingListItemsUseCase,
    private val userLookup: UserLookup,
    private val timezoneSettings: TimezoneSettings,
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
                optional<ShoppingListItemSortMcpDto>(
                    name = "sort",
                    description = "Sort order.",
                    default = ShoppingListItemSortMcpDto.CreatedAtDescending,
                )

            handle {
                val output =
                    listShoppingListItemsUseCase(
                        ListShoppingListItemsUseCase.Input(
                            query =
                                ShoppingListItemQuery(
                                    completed = completed(),
                                    limit = limit(),
                                    sort = sort().toDomain(),
                                ),
                        ),
                    )

                success {
                    val users =
                        resolveUsers(
                            userLookup = userLookup,
                            ids = output.items.flatMap { item -> item.referencedUserIds },
                        )

                    ifEmpty(output.items, "No shopping list items found.") {
                        output.items.forEach { item ->
                            val lastEvent = item.completionEvents.lastOrNull()
                            val status =
                                if (item.isCompleted && lastEvent != null) {
                                    "done by ${users.nameOf(lastEvent.byUserId)} at ${lastEvent.at.toMcpString(timezoneSettings)}"
                                } else {
                                    "open"
                                }

                            line(
                                "- [$status] ${item.name} " +
                                    "(id: ${item.id}, createdBy: ${users.nameOf(item.createdByUserId)}, createdAt: ${item.createdAt.toMcpString(timezoneSettings)})",
                            )
                        }
                    }
                }
            }
        }
    }
}
