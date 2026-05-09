package com.felix.livinglink.infrastructure.mongo

interface MongoVersionedDocument<TDocument : MongoVersionedDocument<TDocument>> {
    val id: String

    val version: Long

    fun withVersion(version: Long): TDocument
}
