package com.felix.livinglink.user.delivery.mcp

import com.felix.livinglink.core.config.McpApiKeySettings
import com.felix.livinglink.core.config.McpRequestUser
import com.felix.livinglink.core.config.McpStdioUserSettings
import com.felix.livinglink.core.config.McpTransport
import com.felix.livinglink.core.config.McpTransportSettings
import com.felix.livinglink.user.domain.User
import com.felix.livinglink.user.domain.UserDirectory
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

    private fun McpRequestUser.toDomain(): User =
        User(
            id = id,
            username = username,
        )
}
