package com.felix.livinglink.shoppingList.delivery.mcp.dto

import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListItemReferenceMcpDto(
    val id: String,
    val name: String,
)

fun ShoppingListItem.toMcpReferenceDto(): ShoppingListItemReferenceMcpDto =
    ShoppingListItemReferenceMcpDto(
        id = id,
        name = name,
    )
