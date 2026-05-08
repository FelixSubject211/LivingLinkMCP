package com.felix.livinglink.shoppingList.infrastructure

import com.felix.livinglink.common.CrudRepository
import com.felix.livinglink.shoppingList.domain.ShoppingListItem

class InMemoryShoppingItemRepository : CrudRepository<ShoppingListItem> {
    private val items = mutableMapOf<String, ShoppingListItem>()

    override suspend fun create(entity: ShoppingListItem): ShoppingListItem {
        items[entity.id] = entity
        return entity
    }

    override suspend fun update(entity: ShoppingListItem): ShoppingListItem? {
        if (!items.containsKey(entity.id)) {
            return null
        }

        items[entity.id] = entity
        return entity
    }

    override suspend fun findById(id: String): ShoppingListItem? {
        return items[id]
    }

    override suspend fun findAll(): List<ShoppingListItem> {
        return items.values.toList()
    }
}