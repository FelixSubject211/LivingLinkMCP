package com.felix.livinglink.shoppingList.delivery.mcp.tools

import com.felix.livinglink.core.delivery.mcp.dsl.McpToolDsl.tool
import com.felix.livinglink.core.delivery.mcp.dsl.success
import com.felix.livinglink.core.delivery.mcp.server.McpRequestUser
import com.felix.livinglink.core.delivery.mcp.server.McpToolRegistrar
import com.felix.livinglink.shoppingList.application.CompleteShoppingListItemsUseCase
import com.felix.livinglink.shoppingList.delivery.mcp.dto.ShoppingListItemReferenceMcpDto
import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import io.modelcontextprotocol.kotlin.sdk.server.Server
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single

@Single(binds = [McpToolRegistrar::class])
class CompleteShoppingListItemsTool(
    private val completeShoppingListItemsUseCase: CompleteShoppingListItemsUseCase,
) : McpToolRegistrar {
    override fun register(
        server: Server,
        user: McpRequestUser,
    ) {
        server.tool(
            name = "complete_shopping_list_items",
            description = "Marks one or more shopping list items as completed.",
        ) {
            val ids =
                required<List<String>>(
                    name = "ids",
                    description = "IDs of the items.",
                )

            handle {
                val output =
                    completeShoppingListItemsUseCase(
                        CompleteShoppingListItemsUseCase.Input(
                            byUserId = user.id,
                            ids = ids(),
                        ),
                    )

                success(
                    Output.fromDomain(
                        completedItems = output.completedItems,
                        alreadyCompletedItems = output.alreadyCompletedItems,
                        missingIds = output.missingIds,
                        conflictedIds = output.conflictedIds,
                    ),
                )
            }
        }
    }

    @Serializable
    private data class Output(
        val completedItems: List<ShoppingListItemReferenceMcpDto>,
        val alreadyCompletedItems: List<ShoppingListItemReferenceMcpDto>,
        val missingIds: List<String>,
        val conflictedIds: List<String>,
    ) {
        companion object {
            fun fromDomain(
                completedItems: List<ShoppingListItem>,
                alreadyCompletedItems: List<ShoppingListItem>,
                missingIds: List<String>,
                conflictedIds: List<String>,
            ): Output =
                Output(
                    completedItems =
                        completedItems.map { item ->
                            ShoppingListItemReferenceMcpDto.fromDomain(item)
                        },
                    alreadyCompletedItems =
                        alreadyCompletedItems.map { item ->
                            ShoppingListItemReferenceMcpDto.fromDomain(item)
                        },
                    missingIds = missingIds,
                    conflictedIds = conflictedIds,
                )
        }
    }
}
