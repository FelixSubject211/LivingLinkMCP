package com.felix.livinglink.features.application

import com.felix.livinglink.contexts.shoppingList.domain.ShoppingListItem
import com.felix.livinglink.contexts.shoppingList.domain.ShoppingListItemQuery
import com.felix.livinglink.contexts.shoppingList.domain.ShoppingListItemRepository
import org.koin.core.annotation.Single

@Single
class ListShoppingListItemsUseCase(
    private val shoppingListItemRepository: ShoppingListItemRepository,
) {
    suspend operator fun invoke(query: ShoppingListItemQuery): List<ShoppingListItem> =
        shoppingListItemRepository.find(query)
}
