package com.felix.livinglink.shoppingList.application

import com.felix.livinglink.common.CrudRepository
import com.felix.livinglink.shoppingList.domain.ShoppingListItem

class CompleteShoppingListItemUseCase(
    private val repository: CrudRepository<ShoppingListItem>
) {
    suspend operator fun invoke(id: String): ShoppingListItem? {
        val item = repository.findById(id) ?: return null
        val completedItem = item.copy(completed = true)

        return repository.update(completedItem)
    }
}