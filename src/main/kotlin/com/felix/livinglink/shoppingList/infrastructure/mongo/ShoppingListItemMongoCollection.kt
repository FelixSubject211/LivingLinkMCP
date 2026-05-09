package com.felix.livinglink.shoppingList.infrastructure.mongo

import com.felix.livinglink.infrastructure.mongo.MongoClientProvider
import com.mongodb.kotlin.client.coroutine.MongoCollection
import org.koin.core.annotation.Single

@Single
fun shoppingListItemMongoCollection(
    mongoClientProvider: MongoClientProvider,
): MongoCollection<MongoShoppingListItemDocument> =
    mongoClientProvider
        .database()
        .getCollection("shopping_list_items")
