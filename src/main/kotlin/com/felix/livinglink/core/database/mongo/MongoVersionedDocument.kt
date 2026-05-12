package com.felix.livinglink.core.database.mongo

interface MongoVersionedDocument<TDocument : MongoVersionedDocument<TDocument>> {
    val id: String

    val version: Long

    fun withVersion(version: Long): TDocument
}
