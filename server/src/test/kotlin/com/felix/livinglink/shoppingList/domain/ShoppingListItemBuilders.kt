package com.felix.livinglink.shoppingList.domain

import com.felix.livinglink.server.shoppingList.domain.ShoppingListItem
import kotlin.time.Instant

private val defaultInstant = Instant.fromEpochSeconds(0)

fun shoppingListItem(
    id: String = "id-1",
    name: String = "item-$id",
    createdByUserId: String = "creator",
    completionEvents: List<ShoppingListItem.CompletionEvent> = emptyList(),
    createdAt: Instant = defaultInstant,
    updatedAt: Instant = createdAt,
    version: Long = 0,
): ShoppingListItem =
    ShoppingListItem(
        id = id,
        name = name,
        createdByUserId = createdByUserId,
        completionEvents = completionEvents,
        createdAt = createdAt,
        updatedAt = updatedAt,
        version = version,
    )

fun completionEvent(
    byUserId: String = "creator",
    completed: Boolean = true,
    at: Instant = defaultInstant,
): ShoppingListItem.CompletionEvent =
    ShoppingListItem.CompletionEvent(
        byUserId = byUserId,
        completed = completed,
        at = at,
    )
