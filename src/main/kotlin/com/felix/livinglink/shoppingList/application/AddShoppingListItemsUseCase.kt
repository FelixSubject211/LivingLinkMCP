package com.felix.livinglink.shoppingList.application

import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import com.felix.livinglink.shoppingList.domain.ShoppingListItemRepository
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class AddShoppingListItemsUseCase(
    private val shoppingListItemRepository: ShoppingListItemRepository,
) {
    suspend operator fun invoke(names: List<String>): List<ShoppingListItem> {
        val cleanedNames =
            names
                .map { name ->
                    name.trim()
                }.filter { name ->
                    name.isNotBlank()
                }.distinct()

        require(cleanedNames.isNotEmpty()) {
            "At least one shopping list item name is required."
        }

        return cleanedNames.map { name ->
            shoppingListItemRepository.create(
                ShoppingListItem(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    completed = false,
                ),
            )
        }
    }
}
