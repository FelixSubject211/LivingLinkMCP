package com.felix.livinglink.core.infrastructure.mongo

import com.felix.livinglink.core.infrastructure.system.Env
import org.koin.core.annotation.Single

@Single
class MongoSettings {
    val connectionString: String =
        Env.required("LIVINGLINK_MONGO_CONNECTION_STRING")

    val databaseName: String =
        Env.required("LIVINGLINK_MONGO_DATABASE")
}
