package com.felix.livinglink.core.database.mongo

import com.felix.livinglink.core.domain.CrudRepository
import com.felix.livinglink.core.domain.OptimisticLockException
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

class MongoCrudRepository<TDocument : MongoVersionedDocument<TDocument>>(
    private val collection: MongoCollection<TDocument>,
    private val entityName: String,
) : CrudRepository<TDocument> {
    override suspend fun create(value: TDocument): TDocument {
        val document = value.withVersion(0)

        collection.insertOne(document)

        return document
    }

    override suspend fun update(value: TDocument): TDocument? {
        val updatedDocument = value.withVersion(value.version + 1)

        val result =
            collection.replaceOne(
                filter =
                    and(
                        eq("_id", value.id),
                        eq("version", value.version),
                    ),
                replacement = updatedDocument,
            )

        if (result.modifiedCount == 1L) {
            return updatedDocument
        }

        findById(value.id) ?: return null

        throw OptimisticLockException(
            "$entityName '${value.id}' was changed concurrently.",
        )
    }

    override suspend fun findById(id: String): TDocument? =
        collection
            .find(eq("_id", id))
            .firstOrNull()

    override suspend fun findAll(): List<TDocument> =
        collection
            .find()
            .toList()
}
