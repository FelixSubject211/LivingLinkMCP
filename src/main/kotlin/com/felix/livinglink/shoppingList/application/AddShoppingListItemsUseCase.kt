package com.felix.livinglink.shoppingList.application

import com.felix.livinglink.core.domain.TimeProvider
import com.felix.livinglink.core.domain.UuidGenerator
import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import com.felix.livinglink.shoppingList.domain.ShoppingListItemRepository
import org.koin.core.annotation.Single

@Single
class AddShoppingListItemsUseCase(
    private val shoppingListItemRepository: ShoppingListItemRepository,
    private val uuidGenerator: UuidGenerator,
    private val timeProvider: TimeProvider,
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

        val now = timeProvider()

        return cleanedNames.map { name ->
            shoppingListItemRepository.create(
                ShoppingListItem(
                    id = uuidGenerator(),
                    name = name,
                    completed = false,
                    createdAt = now,
                    updatedAt = now,
                ),
            )
        }
    }
}
