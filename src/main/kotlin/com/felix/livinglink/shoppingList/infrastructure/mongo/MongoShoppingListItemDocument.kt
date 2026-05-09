package com.felix.livinglink.shoppingList.infrastructure.mongo

import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty

data class MongoShoppingListItemDocument(
    @param:BsonId
    val id: String,
    val name: String,
    val completed: Boolean,
    @param:BsonProperty("version")
    val version: Long,
) {
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
