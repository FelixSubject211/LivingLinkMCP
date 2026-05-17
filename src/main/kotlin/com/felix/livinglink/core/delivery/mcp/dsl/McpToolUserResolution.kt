package com.felix.livinglink.core.delivery.mcp.dsl

import com.felix.livinglink.core.domain.User
import com.felix.livinglink.core.domain.UserLookup

class ResolvedUsers(
    private val usersById: Map<String, User>,
) {
    fun nameOf(id: String): String =
        usersById[id]?.username ?: id
}

suspend fun McpToolResponseBuilder.resolveUsers(
    userLookup: UserLookup,
    ids: Iterable<String>,
): ResolvedUsers =
    ResolvedUsers(
        usersById =
            userLookup.findByIds(
                ids.toSet().toList(),
            ),
    )
