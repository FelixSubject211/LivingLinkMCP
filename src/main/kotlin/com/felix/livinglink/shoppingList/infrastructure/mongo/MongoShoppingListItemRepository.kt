package com.felix.livinglink.shoppingList.infrastructure.mongo

import com.felix.livinglink.core.CrudRepository
import com.felix.livinglink.core.MappedCrudRepository
import com.felix.livinglink.infrastructure.mongo.MongoClientProvider
import com.felix.livinglink.infrastructure.mongo.MongoCrudRepository
import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import com.felix.livinglink.shoppingList.domain.ShoppingListItemRepository
import org.koin.core.annotation.Single

@Single(binds = [ShoppingListItemRepository::class])
class MongoShoppingListItemRepository(
    mongoClientProvider: MongoClientProvider,
) : ShoppingListItemRepository,
    CrudRepository<ShoppingListItem> by MappedCrudRepository(
        storageRepository =
            MongoCrudRepository(
                collection =
                    mongoClientProvider
                        .database()
                        .getCollection<MongoShoppingListItemDocument>("shopping_list_items"),
                entityName = "Shopping list item",
            ),
        toStorage = MongoShoppingListItemDocument::fromDomain,
        toDomain = MongoShoppingListItemDocument::toDomain,
    )
