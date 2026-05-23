package com.felix.livinglink.core.delivery.mcp.server

import com.felix.livinglink.core.config.McpRequestUser
import com.felix.livinglink.core.config.TimezoneSettings
import com.felix.livinglink.user.domain.User
import com.felix.livinglink.user.domain.UserDirectory
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.types.Implementation
import io.modelcontextprotocol.kotlin.sdk.types.ServerCapabilities
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Single

@Single(binds = [McpServerFactory::class])
class LivingLinkMcpServerFactory(
    private val toolRegistrars: List<McpToolRegistrar>,
    private val timezoneSettings: TimezoneSettings,
    private val userDirectory: UserDirectory,
) : McpServerFactory {
    private val cachedUsers: List<User> by lazy {
        runBlocking { userDirectory.all() }
    }

    override fun create(user: McpRequestUser): Server =
        Server(
            serverInfo =
                Implementation(
                    name = "livinglink",
                    version = "0.1.0",
                ),
            options =
                ServerOptions(
                    capabilities =
                        ServerCapabilities(
                            tools = ServerCapabilities.Tools(listChanged = true),
                        ),
                ),
            instructions = buildInstructions(),
        ).also { server ->
            toolRegistrars.forEach { toolRegistrar ->
                toolRegistrar.register(
                    server = server,
                    user = user,
                )
            }
        }

    private fun buildInstructions(): String =
        buildString {
            append("All timestamps are in ")
            append(timezoneSettings.timezoneId)
            append(". Display them as-is without conversion.")
            appendLine()
            appendLine()
            append("Known users (use these IDs when referencing participants or assignees):")
            appendLine()
            cachedUsers.forEach { user ->
                append("- id=")
                append(user.id)
                append(", username=")
                append(user.username)
                appendLine()
            }
        }.trim()
}
