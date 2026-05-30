package com.felix.livinglink.user.config

import com.felix.livinglink.core.config.McpApiKeySettings
import com.felix.livinglink.core.config.McpStdioUserSettings
import com.felix.livinglink.core.config.McpTransport
import com.felix.livinglink.core.config.McpTransportSettings
import com.felix.livinglink.user.domain.User
import com.felix.livinglink.user.domain.UserLookup
import org.koin.core.annotation.Single

@Single(binds = [UserLookup::class])
class EnvUserLookup(
    private val transportSettings: McpTransportSettings,
    private val apiKeySettings: McpApiKeySettings,
    private val stdioUserSettings: McpStdioUserSettings,
) : UserLookup {
    override suspend fun findByIds(ids: Set<String>): Map<String, User> =
        when (transportSettings.transport) {
            McpTransport.Stdio -> {
                val user = stdioUserSettings.user
                if (user.id in ids) {
                    mapOf(user.id to user.toDomain())
                } else {
                    emptyMap()
                }
            }

            McpTransport.Http ->
                apiKeySettings
                    .usersByIds(ids)
                    .mapValues { (_, user) ->
                        user.toDomain()
                    }
        }
}
