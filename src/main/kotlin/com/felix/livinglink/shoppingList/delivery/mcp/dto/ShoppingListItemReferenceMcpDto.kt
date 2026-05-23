package com.felix.livinglink.shoppingList.delivery.mcp.dto

import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListItemReferenceMcpDto(
    val id: String,
    val name: String,
) {
    companion object {
        fun fromDomain(item: ShoppingListItem): ShoppingListItemReferenceMcpDto =
            ShoppingListItemReferenceMcpDto(
                id = item.id,
                name = item.name,
            )
    }
}
