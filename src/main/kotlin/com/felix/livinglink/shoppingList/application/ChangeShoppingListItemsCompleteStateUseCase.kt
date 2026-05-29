package com.felix.livinglink.shoppingList.application

import com.felix.livinglink.core.domain.TimeProvider
import com.felix.livinglink.core.domain.UpdateOperationResult
import com.felix.livinglink.core.domain.UpdateResult
import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import com.felix.livinglink.shoppingList.domain.ShoppingListItemRepository
import org.koin.core.annotation.Single

@Single
class ChangeShoppingListItemsCompleteStateUseCase(
    private val shoppingListItemRepository: ShoppingListItemRepository,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(input: Input): Output {
        val results =
            input.idsToCompleteState.map { (id, completeAction) ->
                val result =
                    shoppingListItemRepository.updateWithOptimisticLocking(id) { current ->
                        when (completeAction) {
                            true -> {
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
                            false -> {
                                if (!current.isCompleted) {
                                    UpdateOperationResult.noUpdate(current = current)
                                } else {
                                    val unCompleted =
                                        current.unComplete(
                                            byUserId = input.byUserId,
                                            at = timeProvider(),
                                        )
                                    UpdateOperationResult.updated(newEntity = unCompleted)
                                }
                            }
                        }
                    }

                when (result) {
                    is UpdateResult.NotFound -> ItemResult.Missing(id)
                    is UpdateResult.OptimisticLockingError -> ItemResult.Conflict(id)
                    is UpdateResult.NotUpdated -> ItemResult.AlreadyChanged(result.response)
                    is UpdateResult.Updated -> ItemResult.Changed(result.newEntity)
                }
            }

        return Output(
            changedItems = results.filterIsInstance<ItemResult.Changed>().map { it.item },
            alreadyChangedItems = results.filterIsInstance<ItemResult.AlreadyChanged>().map { it.item },
            missingIds = results.filterIsInstance<ItemResult.Missing>().map { it.id },
            conflictedIds = results.filterIsInstance<ItemResult.Conflict>().map { it.id },
        )
    }

    private sealed class ItemResult {
        data class Changed(
            val item: ShoppingListItem,
        ) : ItemResult()

        data class AlreadyChanged(
            val item: ShoppingListItem,
        ) : ItemResult()

        data class Missing(
            val id: String,
        ) : ItemResult()

        data class Conflict(
            val id: String,
        ) : ItemResult()
    }

    data class Input(
        val byUserId: String,
        val idsToCompleteState: Map<String, Boolean>,
    )

    data class Output(
        val changedItems: List<ShoppingListItem>,
        val alreadyChangedItems: List<ShoppingListItem>,
        val missingIds: List<String>,
        val conflictedIds: List<String>,
    )
}
