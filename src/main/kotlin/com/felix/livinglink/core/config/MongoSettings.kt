package com.felix.livinglink.core.config

import org.koin.core.annotation.Single

@Single
class MongoSettings(
    val connectionString: String = Env.required("LIVINGLINK_MONGO_CONNECTION_STRING"),
    val databaseName: String = Env.required("LIVINGLINK_MONGO_DATABASE"),
)
