package com.felix.livinglink.user.infrastructure

import com.felix.livinglink.core.delivery.mcp.server.McpApiKeySettings
import com.felix.livinglink.core.delivery.mcp.server.McpRequestUser
import com.felix.livinglink.core.delivery.mcp.server.McpStdioUserSettings
import com.felix.livinglink.core.delivery.mcp.server.McpTransport
import com.felix.livinglink.core.delivery.mcp.server.McpTransportSettings
import com.felix.livinglink.user.domain.User
import com.felix.livinglink.user.domain.UserLookup
import org.koin.core.annotation.Single

@Single(binds = [UserLookup::class])
class EnvUserLookup(
    private val transportSettings: McpTransportSettings,
    private val apiKeySettings: McpApiKeySettings,
    private val stdioUserSettings: McpStdioUserSettings,
) : UserLookup {
    override suspend fun findByIds(ids: List<String>): Map<String, User> {
        val distinctIds = ids.toSet()

        return when (transportSettings.transport) {
            McpTransport.Stdio -> {
                val user = stdioUserSettings.user
                if (user.id in distinctIds) {
                    mapOf(user.id to user.toDomain())
                } else {
                    emptyMap()
                }
            }

            McpTransport.Http ->
                apiKeySettings
                    .usersByIds(distinctIds.toList())
                    .mapValues { (_, user) ->
                        user.toDomain()
                    }
        }
    }

    private fun McpRequestUser.toDomain(): User =
        User(
            id = id,
            username = username,
        )
}
