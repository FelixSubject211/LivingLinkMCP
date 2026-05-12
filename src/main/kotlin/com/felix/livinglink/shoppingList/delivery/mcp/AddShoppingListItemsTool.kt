package com.felix.livinglink.shoppingList.delivery.mcp

import com.felix.livinglink.core.delivery.mcp.McpToolRegistrar
import com.felix.livinglink.core.delivery.mcp.catchingToolErrors
import com.felix.livinglink.core.delivery.mcp.stringArrayPropertySchema
import com.felix.livinglink.core.delivery.mcp.stringListArgument
import com.felix.livinglink.core.delivery.mcp.toolSuccess
import com.felix.livinglink.shoppingList.application.AddShoppingListItemsUseCase
import io.modelcontextprotocol.kotlin.sdk.server.Server
import org.koin.core.annotation.Single

@Single(binds = [McpToolRegistrar::class])
class AddShoppingListItemsTool(
    private val addShoppingListItemsUseCase: AddShoppingListItemsUseCase,
) : McpToolRegistrar {
    override fun register(server: Server) {
        server.addTool(
            name = "add_shopping_list_items",
            description = "Adds one or more items to the shopping list.",
            inputSchema =
                stringArrayPropertySchema(
                    name = "names",
                    description = "Names of the items.",
                ),
        ) { request ->
            catchingToolErrors {
                val items =
                    addShoppingListItemsUseCase(
                        request.params.arguments.stringListArgument("names"),
                    )

                toolSuccess(
                    items.joinToString(separator = "\n") { item ->
                        "- Added '${item.name}' with id '${item.id}'."
                    },
                )
            }
        }
    }
}
