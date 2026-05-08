package com.felix.livinglink.shoppingList.application

import com.felix.livinglink.common.CrudRepository
import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import java.util.UUID

class AddShoppingListItemUseCase(
    private val repository: CrudRepository<ShoppingListItem>,
) {
    suspend operator fun invoke(name: String): ShoppingListItem {
        val item =
            ShoppingListItem(
                id = UUID.randomUUID().toString(),
                name = name.trim(),
                completed = false,
            )

        return repository.create(item)
    }
}
