package com.felix.livinglink.shoppingList.application

import com.felix.livinglink.core.domain.TimeProvider
import com.felix.livinglink.core.domain.retryOptimisticLock
import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import com.felix.livinglink.shoppingList.domain.ShoppingListItemRepository
import org.koin.core.annotation.Single

@Single
class CompleteShoppingListItemsUseCase(
    private val shoppingListItemRepository: ShoppingListItemRepository,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(input: Input): Output {
        val completedItems = mutableListOf<ShoppingListItem>()
        val alreadyCompletedItems = mutableListOf<ShoppingListItem>()
        val missingIds = mutableListOf<String>()

        input.ids.distinct().forEach { id ->
            retryOptimisticLock {
                val item =
                    shoppingListItemRepository.findById(id) ?: run {
                        missingIds += id
                        return@retryOptimisticLock
                    }
                if (item.isCompleted) {
                    alreadyCompletedItems += item
                    return@retryOptimisticLock
                }
                val completedItem =
                    shoppingListItemRepository.update(
                        item.complete(byUserId = input.byUserId, at = timeProvider()),
                    ) ?: run {
                        missingIds += id
                        return@retryOptimisticLock
                    }
                completedItems += completedItem
            }
        }

        return Output(
            completedItems = completedItems.toList(),
            alreadyCompletedItems = alreadyCompletedItems.toList(),
            missingIds = missingIds.toList(),
        )
    }

    data class Input(
        val byUserId: String,
        val ids: List<String>,
    )

    data class Output(
        val completedItems: List<ShoppingListItem>,
        val alreadyCompletedItems: List<ShoppingListItem>,
        val missingIds: List<String>,
    )
}
