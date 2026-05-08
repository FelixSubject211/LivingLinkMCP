package com.felix.livinglink

import com.felix.livinglink.mcp.LivingLinkMcpServer
import com.felix.livinglink.mcp.McpRunner
import com.felix.livinglink.shoppingList.application.AddShoppingListItemUseCase
import com.felix.livinglink.shoppingList.application.CompleteShoppingListItemUseCase
import com.felix.livinglink.shoppingList.application.ListShoppingListItemsUseCase
import com.felix.livinglink.shoppingList.infrastructure.InMemoryShoppingItemRepository

suspend fun main() {
    System.setProperty(
        "kotlin.logging.internal.platform.kotlinLoggingStartupMessageEnabled",
        "false",
    )

    val shoppingItemRepository = InMemoryShoppingItemRepository()

    val server =
        LivingLinkMcpServer.create(
            addShoppingListItemUseCase = AddShoppingListItemUseCase(shoppingItemRepository),
            completeShoppingListItemUseCase = CompleteShoppingListItemUseCase(shoppingItemRepository),
            listShoppingListItemsUseCase = ListShoppingListItemsUseCase(shoppingItemRepository),
        )

    McpRunner.run(server)
}
