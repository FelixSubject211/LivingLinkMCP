package com.felix.livinglink.shoppingList.domain

import kotlin.time.Instant

data class ShoppingListItem(
    val id: String,
    val name: String,
    val createdByUserId: String,
    val completionEvents: List<CompletionEvent>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val version: Long = 0,
) {
    val isCompleted: Boolean
        get() = completionEvents.lastOrNull()?.completed == true

    fun complete(byUserId: String, at: Instant): ShoppingListItem =
        copy(
            updatedAt = at,
            completionEvents =
                completionEvents +
                    CompletionEvent(
                        byUserId = byUserId,
                        completed = true,
                        at = at,
                    ),
        )

    data class CompletionEvent(
        val byUserId: String,
        val completed: Boolean,
        val at: Instant,
    )
}
