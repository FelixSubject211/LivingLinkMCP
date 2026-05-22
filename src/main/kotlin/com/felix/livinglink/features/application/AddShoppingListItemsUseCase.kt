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
    suspend operator fun invoke(input: Input): Output {
        val items =
            input.names.map { name ->
                val now = timeProvider()
                shoppingListItemRepository.create(
                    ShoppingListItem(
                        id = uuidGenerator(),
                        name = name,
                        createdByUserId = input.byUserId,
                        completionEvents = emptyList(),
                        createdAt = now,
                        updatedAt = now,
                    ),
                )
            }
        return Output(items = items)
    }

    data class Input(
        val byUserId: String,
        val names: List<String>,
    )

    data class Output(
        val items: List<ShoppingListItem>,
    )
}
