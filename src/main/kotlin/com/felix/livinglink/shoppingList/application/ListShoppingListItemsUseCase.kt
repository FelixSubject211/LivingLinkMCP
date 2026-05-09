package com.felix.livinglink.shoppingList.application

import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import com.felix.livinglink.shoppingList.domain.ShoppingListItemQuery
import com.felix.livinglink.shoppingList.domain.ShoppingListItemRepository
import org.koin.core.annotation.Single

@Single
class ListShoppingListItemsUseCase(
    private val shoppingListItemRepository: ShoppingListItemRepository,
) {
    suspend operator fun invoke(query: ShoppingListItemQuery): List<ShoppingListItem> =
        shoppingListItemRepository.find(query)
}
