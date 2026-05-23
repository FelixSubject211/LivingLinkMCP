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

    companion object {
        fun fromDomain(
            item: ShoppingListItem,
            resolvedUsers: ResolvedUsers,
            timezoneSettings: TimezoneSettings,
        ): ShoppingListItemDetailMcpDto =
            ShoppingListItemDetailMcpDto(
                id = item.id,
                name = item.name,
                completed = item.isCompleted,
                completionEvents =
                    item.completionEvents.map { event ->
                        CompletionEventMcpDto(
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
                        id = item.createdByUserId,
                        resolvedUsers = resolvedUsers,
                    ),
                createdAt = item.createdAt.toMcpString(timezoneSettings),
                updatedAt = item.updatedAt.toMcpString(timezoneSettings),
            )
    }
}
