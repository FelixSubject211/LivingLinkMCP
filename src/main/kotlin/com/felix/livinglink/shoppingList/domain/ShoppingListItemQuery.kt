package com.felix.livinglink.shoppingList.domain

data class ShoppingListItemQuery(
    val completed: Boolean? = null,
    val limit: Int,
    val sort: ShoppingListItemSort = ShoppingListItemSort.CreatedAtDescending,
)

enum class ShoppingListItemSort {
    CreatedAtAscending,
    CreatedAtDescending,
    UpdatedAtAscending,
    UpdatedAtDescending,
    NameAscending,
    NameDescending,
}
