package com.felix.livinglink.features.delivery.mcp.tools

import com.felix.livinglink.core.delivery.mcp.dsl.McpToolDsl.tool
import com.felix.livinglink.core.delivery.mcp.dsl.success
import com.felix.livinglink.core.delivery.mcp.server.McpRequestUser
import com.felix.livinglink.core.delivery.mcp.server.McpToolRegistrar
import com.felix.livinglink.features.application.AddShoppingListItemsUseCase
import io.modelcontextprotocol.kotlin.sdk.server.Server
import org.koin.core.annotation.Single

@Single(binds = [McpToolRegistrar::class])
class AddShoppingListItemsTool(
    private val addShoppingListItemsUseCase: AddShoppingListItemsUseCase,
) : McpToolRegistrar {
    override fun register(
        server: Server,
        user: McpRequestUser,
    ) {
        server.tool(
            name = "add_shopping_list_items",
            description = "Adds one or more items to the shopping list.",
        ) {
            val names =
                required<List<String>>(
                    name = "names",
                    description = "Names of the items.",
                )

            handle {
                val output =
                    addShoppingListItemsUseCase(
                        AddShoppingListItemsUseCase.Input(
                            byUserId = user.id,
                            names = names(),
                        ),
                    )

                success {
                    output.items.forEach { item ->
                        line("- Added '${item.name}' with id '${item.id}'.")
                    }
                }
            }
        }
    }
}
