package com.felix.livinglink.user.delivery.mcp

import com.felix.livinglink.user.domain.User
import com.felix.livinglink.user.domain.UserLookup

class ResolvedUsers(
    private val usersById: Map<String, User>,
) {
    fun nameOf(id: String): String =
        usersById[id]?.username ?: id
}

suspend fun resolveUsers(
    userLookup: UserLookup,
    ids: Iterable<String>,
): ResolvedUsers =
    ResolvedUsers(
        usersById =
            userLookup.findByIds(
                ids.toSet(),
            ),
    )
