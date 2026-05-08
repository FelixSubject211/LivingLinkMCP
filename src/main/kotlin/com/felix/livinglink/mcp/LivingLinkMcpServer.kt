package com.felix.livinglink.mcp

import com.felix.livinglink.mcp.shoppingList.registerAddShoppingListItemTool
import com.felix.livinglink.mcp.shoppingList.registerCompleteShoppingListItemTool
import com.felix.livinglink.mcp.shoppingList.registerListShoppingListItemsTool
import com.felix.livinglink.shoppingList.application.AddShoppingListItemUseCase
import com.felix.livinglink.shoppingList.application.CompleteShoppingListItemUseCase
import com.felix.livinglink.shoppingList.application.ListShoppingListItemsUseCase
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.types.Implementation
import io.modelcontextprotocol.kotlin.sdk.types.ServerCapabilities

object LivingLinkMcpServer {
    fun create(
        addShoppingListItemUseCase: AddShoppingListItemUseCase,
        completeShoppingListItemUseCase: CompleteShoppingListItemUseCase,
        listShoppingListItemsUseCase: ListShoppingListItemsUseCase,
    ): Server =
        Server(
            serverInfo =
                Implementation(
                    name = "livinglink",
                    version = "0.1.0",
                ),
            options =
                ServerOptions(
                    capabilities =
                        ServerCapabilities(
                            tools = ServerCapabilities.Tools(listChanged = true),
                        ),
                ),
        ).apply {
            registerAddShoppingListItemTool(addShoppingListItemUseCase)
            registerCompleteShoppingListItemTool(completeShoppingListItemUseCase)
            registerListShoppingListItemsTool(listShoppingListItemsUseCase)
        }
}
