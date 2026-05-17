package com.felix.livinglink.features.application

import com.felix.livinglink.contexts.shoppingList.domain.ShoppingListItem
import com.felix.livinglink.contexts.shoppingList.domain.ShoppingListItemRepository
import com.felix.livinglink.core.domain.TimeProvider
import com.felix.livinglink.core.domain.UuidGenerator
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
    ): List<ShoppingListItem> =
        names.map { name ->
            val now = timeProvider()

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
