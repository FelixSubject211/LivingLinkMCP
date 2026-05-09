package com.felix.livinglink.shoppingList.domain

import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListItem(
    val id: String,
    val name: String,
    val completed: Boolean,
) {
    fun complete(): ShoppingListItem =
        copy(completed = true)
}
