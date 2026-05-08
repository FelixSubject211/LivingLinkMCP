package com.felix.livinglink.di

import com.felix.livinglink.common.CrudRepository
import com.felix.livinglink.mcp.LivingLinkMcpServer
import com.felix.livinglink.shoppingList.application.AddShoppingListItemUseCase
import com.felix.livinglink.shoppingList.application.CompleteShoppingListItemUseCase
import com.felix.livinglink.shoppingList.application.ListShoppingListItemsUseCase
import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import com.felix.livinglink.shoppingList.infrastructure.InMemoryShoppingItemRepository
import io.modelcontextprotocol.kotlin.sdk.server.Server
import org.koin.dsl.module

val livingLinkModule =
    module {
        single<CrudRepository<ShoppingListItem>> {
            InMemoryShoppingItemRepository()
        }

        single {
            AddShoppingListItemUseCase(get())
        }

        single {
            CompleteShoppingListItemUseCase(get())
        }

        single {
            ListShoppingListItemsUseCase(get())
        }

        single<Server> {
            LivingLinkMcpServer.create(
                addShoppingListItemUseCase = get(),
                completeShoppingListItemUseCase = get(),
                listShoppingListItemsUseCase = get(),
            )
        }
    }
