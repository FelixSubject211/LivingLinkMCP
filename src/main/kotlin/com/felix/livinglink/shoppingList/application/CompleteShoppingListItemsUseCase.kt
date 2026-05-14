package com.felix.livinglink.shoppingList.application

import com.felix.livinglink.core.domain.TimeProvider
import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import com.felix.livinglink.shoppingList.domain.ShoppingListItemRepository
import org.koin.core.annotation.Single

@Single
class CompleteShoppingListItemsUseCase(
    private val shoppingListItemRepository: ShoppingListItemRepository,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(
        byUserId: String,
        ids: List<String>,
    ): Result {
        val cleanedIds = ids.distinct()

        val now = timeProvider()

        return cleanedIds.fold(Result()) { result, id ->
            val item = shoppingListItemRepository.findById(id) ?: return@fold result.withMissingId(id)

            if (item.isCompleted) {
                return@fold result.withAlreadyCompletedItem(item)
            }

            val completedItem =
                shoppingListItemRepository.update(
                    item.complete(
                        byUserId = byUserId,
                        at = now,
                    ),
                )

            if (completedItem == null) {
                result.withMissingId(id)
            } else {
                result.withCompletedItem(completedItem)
            }
        }
    }

    data class Result(
        val completedItems: List<ShoppingListItem> = emptyList(),
        val alreadyCompletedItems: List<ShoppingListItem> = emptyList(),
        val missingIds: List<String> = emptyList(),
    ) {
        fun withCompletedItem(item: ShoppingListItem): Result =
            copy(
                completedItems = completedItems + item,
            )

        fun withAlreadyCompletedItem(item: ShoppingListItem): Result =
            copy(
                alreadyCompletedItems = alreadyCompletedItems + item,
            )

        fun withMissingId(id: String): Result =
            copy(
                missingIds = missingIds + id,
            )
    }
}
