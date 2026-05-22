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
