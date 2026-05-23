package com.felix.livinglink.shoppingList.delivery.mcp.tools

import com.felix.livinglink.core.delivery.mcp.dsl.McpToolDsl.tool
import com.felix.livinglink.core.delivery.mcp.dsl.success
import com.felix.livinglink.core.delivery.mcp.server.McpRequestUser
import com.felix.livinglink.core.delivery.mcp.server.McpToolRegistrar
import com.felix.livinglink.core.infrastructure.system.TimezoneSettings
import com.felix.livinglink.shoppingList.application.ListShoppingListItemsUseCase
import com.felix.livinglink.shoppingList.delivery.mcp.dto.ShoppingListItemDetailMcpDto
import com.felix.livinglink.shoppingList.delivery.mcp.dto.ShoppingListItemSortMcpDto
import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import com.felix.livinglink.shoppingList.domain.ShoppingListItemQuery
import com.felix.livinglink.user.delivery.mcp.ResolvedUsers
import com.felix.livinglink.user.delivery.mcp.resolveUsers
import com.felix.livinglink.user.domain.UserLookup
import io.modelcontextprotocol.kotlin.sdk.server.Server
import kotlinx.serialization.Serializable
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

                val resolvedUsers =
                    resolveUsers(
                        userLookup = userLookup,
                        ids = output.items.flatMap { item -> item.referencedUserIds },
                    )

                success(
                    Output.fromDomain(
                        items = output.items,
                        resolvedUsers = resolvedUsers,
                        timezoneSettings = timezoneSettings,
                    ),
                )
            }
        }
    }

    @Serializable
    private data class Output(
        val items: List<ShoppingListItemDetailMcpDto>,
    ) {
        companion object {
            fun fromDomain(
                items: List<ShoppingListItem>,
                resolvedUsers: ResolvedUsers,
                timezoneSettings: TimezoneSettings,
            ): Output =
                Output(
                    items =
                        items.map { item ->
                            ShoppingListItemDetailMcpDto.fromDomain(
                                item = item,
                                resolvedUsers = resolvedUsers,
                                timezoneSettings = timezoneSettings,
                            )
                        },
                )
        }
    }
}
