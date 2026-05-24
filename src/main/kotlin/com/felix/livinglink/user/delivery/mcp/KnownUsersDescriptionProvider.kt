package com.felix.livinglink.user.delivery.mcp

import com.felix.livinglink.user.domain.UserDirectory
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Single

@Single
class KnownUsersDescriptionProvider(
    private val userDirectory: UserDirectory,
) {
    fun describeWith(prefix: String): String {
        val users = runBlocking { userDirectory.all() }

        return buildString {
            append(prefix.trimEnd())
            appendLine()
            appendLine()
            append("Known users (use one of these IDs):")
            users.forEach { user ->
                appendLine()
                append("- id=")
                append(user.id)
                append(", username=")
                append(user.username)
            }
        }
    }
}
