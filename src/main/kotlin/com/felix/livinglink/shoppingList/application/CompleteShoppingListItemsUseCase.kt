package com.felix.livinglink.shoppingList.application

import com.felix.livinglink.core.domain.TimeProvider
import com.felix.livinglink.core.domain.UpdateOperationResult
import com.felix.livinglink.core.domain.UpdateResult
import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import com.felix.livinglink.shoppingList.domain.ShoppingListItemRepository
import org.koin.core.annotation.Single

@Single
class CompleteShoppingListItemsUseCase(
    private val shoppingListItemRepository: ShoppingListItemRepository,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(input: Input): Output {
        val results =
            input.ids.distinct().map { id ->
                val result =
                    shoppingListItemRepository.updateWithOptimisticLocking(id) { current ->
                        if (current.isCompleted) {
                            UpdateOperationResult.noUpdate(current = current)
                        } else {
                            val completed =
                                current.complete(
                                    byUserId = input.byUserId,
                                    at = timeProvider(),
                                )
                            UpdateOperationResult.updated(newEntity = completed)
                        }
                    }

                when (result) {
                    is UpdateResult.NotFound -> ItemResult.Missing(id)
                    is UpdateResult.NotUpdated -> ItemResult.AlreadyCompleted(result.response)
                    is UpdateResult.Updated -> ItemResult.Completed(result.newEntity)
                }
            }

        return Output(
            completedItems = results.filterIsInstance<ItemResult.Completed>().map { it.item },
            alreadyCompletedItems = results.filterIsInstance<ItemResult.AlreadyCompleted>().map { it.item },
            missingIds = results.filterIsInstance<ItemResult.Missing>().map { it.id },
        )
    }

    private sealed class ItemResult {
        data class Completed(
            val item: ShoppingListItem,
        ) : ItemResult()

        data class AlreadyCompleted(
            val item: ShoppingListItem,
        ) : ItemResult()

        data class Missing(
            val id: String,
        ) : ItemResult()
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
