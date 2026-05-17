package com.felix.livinglink.contexts.shoppingList.database.mongo

import com.felix.livinglink.core.database.mongo.MongoClientProvider
import com.mongodb.kotlin.client.coroutine.MongoCollection
import org.koin.core.annotation.Single

@Single
fun shoppingListItemMongoCollection(
    mongoClientProvider: MongoClientProvider,
): MongoCollection<MongoShoppingListItemDocument> =
    mongoClientProvider
        .database()
        .getCollection("shopping_list_items")
