package com.felix.livinglink.core.infrastructure.mongo

import com.felix.livinglink.core.config.MongoSettings
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.mongodb.MongoDBContainer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@Testcontainers
abstract class AbstractMongoRepositoryTest {
    companion object {
        @Container
        val mongoContainer = MongoDBContainer("mongo:7")
    }

    protected lateinit var clientProvider: MongoClientProvider
    protected lateinit var database: MongoDatabase

    @BeforeTest
    fun setUpMongo() {
        val testSettings =
            MongoSettings(
                connectionString = mongoContainer.replicaSetUrl,
                databaseName = "livinglink_test",
            )
        clientProvider = MongoClientProvider(testSettings)
        database = clientProvider.database()
    }

    @AfterTest
    fun tearDownMongo() {
        if (::clientProvider.isInitialized) {
            clientProvider.close()
        }
    }
}
