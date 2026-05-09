package com.felix.livinglink.shoppingList.domain

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class ShoppingListItem(
    val id: String,
    val name: String,
    val completed: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
    val version: Long = 0,
) {
    fun complete(now: Instant): ShoppingListItem =
        copy(
            completed = true,
            updatedAt = now,
        )
}
