package com.felix.livinglink.shoppingList.domain

data class ShoppingListItemQuery(
    val completed: Boolean? = null,
    val limit: Int,
    val sort: ShoppingListItemSort = ShoppingListItemSort.CreatedAtDescending,
)

sealed interface ShoppingListItemSort {
    data object CreatedAtAscending : ShoppingListItemSort

    data object CreatedAtDescending : ShoppingListItemSort

    data object UpdatedAtAscending : ShoppingListItemSort

    data object UpdatedAtDescending : ShoppingListItemSort

    data object NameAscending : ShoppingListItemSort

    data object NameDescending : ShoppingListItemSort
}
