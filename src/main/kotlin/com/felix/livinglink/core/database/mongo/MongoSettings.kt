package com.felix.livinglink.core.database.mongo

import org.koin.core.annotation.Single

@Single
class MongoSettings {
    val connectionString: String =
        System.getenv("LIVINGLINK_MONGO_CONNECTION_STRING") ?: "mongodb://localhost:27017"

    val databaseName: String =
        System.getenv("LIVINGLINK_MONGO_DATABASE") ?: "livinglink"
}
