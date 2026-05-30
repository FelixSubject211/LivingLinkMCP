package com.felix.livinglink.session.delivery.mcp.tools

import com.felix.livinglink.calendar.application.GetEventCategoriesUseCase
import com.felix.livinglink.core.config.McpRequestUser
import com.felix.livinglink.core.delivery.mcp.dsl.McpToolDsl.tool
import com.felix.livinglink.core.delivery.mcp.dsl.success
import com.felix.livinglink.core.delivery.mcp.server.McpToolRegistrar
import com.felix.livinglink.session.delivery.mcp.dto.UserMcpDto
import com.felix.livinglink.user.domain.UserDirectory
import io.modelcontextprotocol.kotlin.sdk.server.Server
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single

@Single(binds = [McpToolRegistrar::class])
class GetSessionTool(
    private val userDirectory: UserDirectory,
    private val getEventCategoriesUseCase: GetEventCategoriesUseCase,
) : McpToolRegistrar {
    override fun register(
        server: Server,
        user: McpRequestUser,
    ) {
        server.tool(
            name = "get_session",
            description =
                """
                Call this tool first before doing anything else.
                Returns your current session context, including who you are.
                Also returns known custom calendar event category labels. Prefer reusing
                existing ones for consistency, but feel free to create new ones when it makes sense.
                """.trimIndent(),
        ) {
            handle {
                val allUsers = userDirectory.all()
                val categoriesOutput = getEventCategoriesUseCase()

                success(
                    Output(
                        currentUser =
                            UserMcpDto(
                                id = user.id,
                                username = user.username,
                            ),
                        availableUsers =
                            allUsers
                                .filter { it.id != user.id }
                                .map { u -> UserMcpDto(id = u.id, username = u.username) },
                        knownCustomEventCategoryLabels = categoriesOutput.knownCustomLabels,
                    ),
                )
            }
        }
    }

    @Serializable
    private data class Output(
        val currentUser: UserMcpDto,
        val availableUsers: List<UserMcpDto>,
        val knownCustomEventCategoryLabels: List<String>,
    )
}
