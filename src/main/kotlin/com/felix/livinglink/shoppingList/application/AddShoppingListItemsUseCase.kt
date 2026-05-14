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
    suspend operator fun invoke(
        byUserId: String,
        names: List<String>,
    ): List<ShoppingListItem> {
        val now = timeProvider()

        return names.map { name ->
            shoppingListItemRepository.create(
                ShoppingListItem(
                    id = uuidGenerator(),
                    name = name,
                    createdByUserId = byUserId,
                    completionEvents = emptyList(),
                    createdAt = now,
                    updatedAt = now,
                ),
            )
        }
    }
}
