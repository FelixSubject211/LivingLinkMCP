package com.felix.livinglink.shoppingList.domain

import com.felix.livinglink.core.domain.CrudRepository

interface ShoppingListItemRepository : CrudRepository<ShoppingListItem> {
    suspend fun find(query: ShoppingListItemQuery): List<ShoppingListItem>
}
