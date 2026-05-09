package com.felix.livinglink.shoppingList.application

import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import com.felix.livinglink.shoppingList.domain.ShoppingListItemRepository
import org.koin.core.annotation.Single

@Single
class CompleteShoppingListItemUseCase(
    private val shoppingListItemRepository: ShoppingListItemRepository,
) {
    suspend operator fun invoke(id: String): ShoppingListItem? {
        val item = shoppingListItemRepository.findById(id) ?: return null

        return shoppingListItemRepository.update(item.complete())
    }
}
