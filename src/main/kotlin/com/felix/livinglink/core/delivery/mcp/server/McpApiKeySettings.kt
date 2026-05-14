package com.felix.livinglink.core.delivery.mcp.server

import com.felix.livinglink.core.system.Env
import org.koin.core.annotation.Single

@Single
class McpApiKeySettings {
    private val usersByApiKey: Map<String, McpRequestUser> by lazy {
        Env
            .required("LIVINGLINK_MCP_API_KEYS")
            .split(",")
            .associate { rawEntry ->
                val parts =
                    rawEntry
                        .trim()
                        .split(":", limit = 3)

                require(parts.size == 3) {
                    "Invalid LIVINGLINK_MCP_API_KEYS entry '$rawEntry'. Expected: userId:username:apiKey"
                }

                val userId = parts[0].trim()
                val username = parts[1].trim()
                val apiKey = parts[2].trim()

                require(userId.isNotBlank()) {
                    "Invalid LIVINGLINK_MCP_API_KEYS entry '$rawEntry': userId is blank."
                }
                require(username.isNotBlank()) {
                    "Invalid LIVINGLINK_MCP_API_KEYS entry '$rawEntry': username is blank."
                }
                require(apiKey.isNotBlank()) {
                    "Invalid LIVINGLINK_MCP_API_KEYS entry '$rawEntry': apiKey is blank."
                }

                apiKey to
                    McpRequestUser(
                        id = userId,
                        username = username,
                    )
            }
    }

    private val usersById: Map<String, McpRequestUser> by lazy {
        usersByApiKey.values.associateBy { user ->
            user.id
        }
    }

    fun userForApiKey(apiKey: String): McpRequestUser? =
        usersByApiKey[apiKey]

    fun userById(id: String): McpRequestUser? =
        usersById[id]
}
