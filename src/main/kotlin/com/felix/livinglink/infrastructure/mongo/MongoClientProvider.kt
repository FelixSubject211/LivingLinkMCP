package com.felix.livinglink.infrastructure.mongo

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.koin.core.annotation.Single

@Single
class MongoClientProvider(
    private val mongoSettings: MongoSettings,
) {
    private val client: MongoClient by lazy {
        MongoClient.create(mongoSettings.connectionString)
    }

    fun database(): MongoDatabase =
        client.getDatabase(mongoSettings.databaseName)
}
