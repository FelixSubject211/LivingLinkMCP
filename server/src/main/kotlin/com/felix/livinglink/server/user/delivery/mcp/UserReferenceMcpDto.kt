package com.felix.livinglink.server.user.delivery.mcp

import kotlinx.serialization.Serializable

@Serializable
data class UserReferenceMcpDto(
    val id: String,
    val username: String,
) {
    companion object {
        fun fromResolved(
            id: String,
            resolvedUsers: ResolvedUsers,
        ): UserReferenceMcpDto =
            UserReferenceMcpDto(
                id = id,
                username = resolvedUsers.nameOf(id),
            )
    }
}
