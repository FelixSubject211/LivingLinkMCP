package com.felix.livinglink.shoppingList.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListItemQuery(
    val completed: Boolean? = null,
    val limit: Int,
    val sort: ShoppingListItemSort = ShoppingListItemSort.CreatedAtDescending,
)

@Serializable
enum class ShoppingListItemSort {
    @SerialName("createdAtAsc")
    CreatedAtAscending,

    @SerialName("createdAtDesc")
    CreatedAtDescending,

    @SerialName("updatedAtAsc")
    UpdatedAtAscending,

    @SerialName("updatedAtDesc")
    UpdatedAtDescending,

    @SerialName("nameAsc")
    NameAscending,

    @SerialName("nameDesc")
    NameDescending,
}
