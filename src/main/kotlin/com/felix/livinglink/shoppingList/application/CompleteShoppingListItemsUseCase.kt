package com.felix.livinglink.shoppingList.application

import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import com.felix.livinglink.shoppingList.domain.ShoppingListItemRepository
import org.koin.core.annotation.Single

@Single
class CompleteShoppingListItemsUseCase(
    private val shoppingListItemRepository: ShoppingListItemRepository,
) {
    suspend operator fun invoke(ids: List<String>): Result {
        val cleanedIds =
            ids
                .map { id ->
                    id.trim()
                }.filter { id ->
                    id.isNotBlank()
                }.distinct()

        require(cleanedIds.isNotEmpty()) {
            "At least one shopping list item id is required."
        }

        return cleanedIds.fold(Result()) { result, id ->
            val item = shoppingListItemRepository.findById(id)

            if (item == null) {
                return@fold result.withMissingId(id)
            }

            if (item.completed) {
                return@fold result.withAlreadyCompletedItem(item)
            }

            val completedItem = shoppingListItemRepository.update(item.complete())

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
