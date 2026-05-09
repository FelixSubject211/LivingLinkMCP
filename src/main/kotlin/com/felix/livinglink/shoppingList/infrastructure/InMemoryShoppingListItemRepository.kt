package com.felix.livinglink.shoppingList.infrastructure

import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import com.felix.livinglink.shoppingList.domain.ShoppingListItemRepository
import org.koin.core.annotation.Single

@Single(binds = [ShoppingListItemRepository::class])
class InMemoryShoppingListItemRepository : ShoppingListItemRepository {
    private val items = mutableMapOf<String, ShoppingListItem>()

    override suspend fun create(value: ShoppingListItem): ShoppingListItem {
        items[value.id] = value
        return value
    }

    override suspend fun update(value: ShoppingListItem): ShoppingListItem? {
        if (!items.containsKey(value.id)) {
            return null
        }

        items[value.id] = value
        return value
    }

    override suspend fun findById(id: String): ShoppingListItem? = items[id]

    override suspend fun findAll(): List<ShoppingListItem> = items.values.toList()
}
