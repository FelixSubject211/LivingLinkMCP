package com.felix.livinglink.shoppingList.infrastructure.mongo

import com.felix.livinglink.core.OptimisticLockException
import com.felix.livinglink.infrastructure.mongo.MongoClientProvider
import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import com.felix.livinglink.shoppingList.domain.ShoppingListItemRepository
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.koin.core.annotation.Single

@Single(binds = [ShoppingListItemRepository::class])
class MongoShoppingListItemRepository(
    mongoClientProvider: MongoClientProvider,
) : ShoppingListItemRepository {
    private val collection: MongoCollection<MongoShoppingListItemDocument> =
        mongoClientProvider
            .database()
            .getCollection<MongoShoppingListItemDocument>("shopping_list_items")

    override suspend fun create(value: ShoppingListItem): ShoppingListItem {
        val document =
            MongoShoppingListItemDocument.fromDomain(
                value.copy(version = 0),
            )

        collection.insertOne(document)

        return document.toDomain()
    }

    override suspend fun update(value: ShoppingListItem): ShoppingListItem? {
        val updatedItem =
            value.copy(
                version = value.version + 1,
            )

        val filter =
            and(
                eq("_id", value.id),
                eq("version", value.version),
            )

        val result =
            collection.replaceOne(
                filter = filter,
                replacement = MongoShoppingListItemDocument.fromDomain(updatedItem),
            )

        if (result.modifiedCount == 1L) {
            return updatedItem
        }

        findById(value.id) ?: return null

        throw OptimisticLockException(
            "Shopping list item '${value.id}' was changed concurrently.",
        )
    }

    override suspend fun findById(id: String): ShoppingListItem? =
        collection
            .find(eq("_id", id))
            .firstOrNull()
            ?.toDomain()

    override suspend fun findAll(): List<ShoppingListItem> =
        collection
            .find()
            .map { document ->
                document.toDomain()
            }.toList()
}
