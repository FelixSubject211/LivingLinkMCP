package com.felix.livinglink.shoppingList.infrastructure.mongo

import com.felix.livinglink.infrastructure.mongo.MongoVersionedDocument
import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty

data class MongoShoppingListItemDocument(
    @param:BsonId
    override val id: String,
    val name: String,
    val completed: Boolean,
    @param:BsonProperty("version")
    override val version: Long,
) : MongoVersionedDocument<MongoShoppingListItemDocument> {
    override fun withVersion(version: Long): MongoShoppingListItemDocument =
        copy(version = version)

    fun toDomain(): ShoppingListItem =
        ShoppingListItem(
            id = id,
            name = name,
            completed = completed,
            version = version,
        )

    companion object {
        fun fromDomain(item: ShoppingListItem): MongoShoppingListItemDocument =
            MongoShoppingListItemDocument(
                id = item.id,
                name = item.name,
                completed = item.completed,
                version = item.version,
            )
    }
}
