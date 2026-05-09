package com.felix.livinglink.shoppingList.application

import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import com.felix.livinglink.shoppingList.domain.ShoppingListItemRepository
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class AddShoppingListItemUseCase(
    private val shoppingListItemRepository: ShoppingListItemRepository,
) {
    suspend operator fun invoke(name: String): ShoppingListItem {
        val trimmedName = name.trim()

        require(trimmedName.isNotBlank()) {
            "Shopping list item name must not be blank."
        }

        val item =
            ShoppingListItem(
                id = UUID.randomUUID().toString(),
                name = trimmedName,
                completed = false,
            )

        return shoppingListItemRepository.create(item)
    }
}
