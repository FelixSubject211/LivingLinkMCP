package com.felix.livinglink.core.system

import com.felix.livinglink.core.delivery.mcp.server.McpApiKeySettings
import com.felix.livinglink.core.delivery.mcp.server.McpStdioUserSettings
import com.felix.livinglink.core.delivery.mcp.server.McpTransport
import com.felix.livinglink.core.delivery.mcp.server.McpTransportSettings
import com.felix.livinglink.core.domain.User
import com.felix.livinglink.core.domain.UserLookup
import org.koin.core.annotation.Single

@Single(binds = [UserLookup::class])
class EnvUserLookup(
    private val transportSettings: McpTransportSettings,
    private val apiKeySettings: McpApiKeySettings,
    private val stdioUserSettings: McpStdioUserSettings,
) : UserLookup {
    override suspend fun findById(id: String): User? {
        val mcpUser =
            when (transportSettings.transport) {
                McpTransport.Stdio ->
                    stdioUserSettings.user.takeIf { user ->
                        user.id == id
                    }

                McpTransport.Http ->
                    apiKeySettings.userById(id)
            }

        return mcpUser?.let { user ->
            User(
                id = user.id,
                username = user.username,
            )
        }
    }
}
