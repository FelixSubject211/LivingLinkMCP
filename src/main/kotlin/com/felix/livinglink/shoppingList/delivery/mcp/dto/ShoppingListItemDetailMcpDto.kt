package com.felix.livinglink.shoppingList.delivery.mcp.dto

import com.felix.livinglink.core.delivery.mcp.dsl.toMcpString
import com.felix.livinglink.core.infrastructure.system.TimezoneSettings
import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import com.felix.livinglink.user.delivery.mcp.ResolvedUsers
import com.felix.livinglink.user.delivery.mcp.UserReferenceMcpDto
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
                        UserReferenceMcpDto.fromResolved(
                            id = event.byUserId,
                            resolvedUsers = resolvedUsers,
                        ),
                    at = event.at.toMcpString(timezoneSettings),
                )
            },
        createdBy =
            UserReferenceMcpDto.fromResolved(
                id = createdByUserId,
                resolvedUsers = resolvedUsers,
            ),
        createdAt = createdAt.toMcpString(timezoneSettings),
        updatedAt = updatedAt.toMcpString(timezoneSettings),
    )
