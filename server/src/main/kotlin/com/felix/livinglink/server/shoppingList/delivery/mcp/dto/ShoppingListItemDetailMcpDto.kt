package com.felix.livinglink.server.shoppingList.delivery.mcp.dto

import com.felix.livinglink.server.core.config.TimezoneSettings
import com.felix.livinglink.server.core.delivery.mcp.dsl.toMcpString
import com.felix.livinglink.server.shoppingList.domain.ShoppingListItem
import com.felix.livinglink.server.user.delivery.mcp.ResolvedUsers
import com.felix.livinglink.server.user.delivery.mcp.UserReferenceMcpDto
import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListItemDetailMcpDto(
    val id: String,
    val name: String,
    val createdBy: UserReferenceMcpDto,
    val completed: Boolean,
    val completionEvents: List<CompletionEventMcpDto>,
    val createdAt: String,
    val updatedAt: String,
) {
    @Serializable
    data class CompletionEventMcpDto(
        val by: UserReferenceMcpDto,
        val at: String,
    )
}

fun ShoppingListItem.toMcpDetailDto(
    resolvedUsers: ResolvedUsers,
    timezoneSettings: TimezoneSettings,
): ShoppingListItemDetailMcpDto =
    ShoppingListItemDetailMcpDto(
        id = id,
        name = name,
        completed = isCompleted,
        completionEvents =
            completionEvents.map { event ->
                ShoppingListItemDetailMcpDto.CompletionEventMcpDto(
                    by =
                        UserReferenceMcpDto.Companion.fromResolved(
                            id = event.byUserId,
                            resolvedUsers = resolvedUsers,
                        ),
                    at = event.at.toMcpString(timezoneSettings),
                )
            },
        createdBy =
            UserReferenceMcpDto.Companion.fromResolved(
                id = createdByUserId,
                resolvedUsers = resolvedUsers,
            ),
        createdAt = createdAt.toMcpString(timezoneSettings),
        updatedAt = updatedAt.toMcpString(timezoneSettings),
    )
