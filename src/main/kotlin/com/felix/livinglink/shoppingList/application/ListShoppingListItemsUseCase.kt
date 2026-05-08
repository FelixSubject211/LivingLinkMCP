package com.felix.livinglink.shoppingList.application

import com.felix.livinglink.common.CrudRepository
import com.felix.livinglink.shoppingList.domain.ShoppingListItem

class ListShoppingListItemsUseCase(
    private val repository: CrudRepository<ShoppingListItem>,
) {
    suspend operator fun invoke(): List<ShoppingListItem> = repository.findAll()
}
