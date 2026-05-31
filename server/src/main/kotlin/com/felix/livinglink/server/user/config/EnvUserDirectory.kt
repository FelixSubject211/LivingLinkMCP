package com.felix.livinglink.server.user.config

import com.felix.livinglink.server.core.config.McpApiKeySettings
import com.felix.livinglink.server.core.config.McpStdioUserSettings
import com.felix.livinglink.server.core.config.McpTransport
import com.felix.livinglink.server.core.config.McpTransportSettings
import com.felix.livinglink.server.user.domain.User
import com.felix.livinglink.server.user.domain.UserDirectory
import org.koin.core.annotation.Single

@Single(binds = [UserDirectory::class])
class EnvUserDirectory(
    private val transportSettings: McpTransportSettings,
    private val apiKeySettings: McpApiKeySettings,
    private val stdioUserSettings: McpStdioUserSettings,
) : UserDirectory {
    override suspend fun all(): List<User> =
        when (transportSettings.transport) {
            McpTransport.Stdio -> listOf(stdioUserSettings.user.toDomain())
            McpTransport.Http -> apiKeySettings.allUsers().map { it.toDomain() }
        }
}
