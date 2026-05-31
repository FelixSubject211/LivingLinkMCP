package com.felix.livinglink.server.session.delivery.mcp.tools

import com.felix.livinglink.server.calendar.application.GetEventCategoriesUseCase
import com.felix.livinglink.server.core.config.McpRequestUser
import com.felix.livinglink.server.core.delivery.mcp.dsl.McpToolDsl.tool
import com.felix.livinglink.server.core.delivery.mcp.dsl.success
import com.felix.livinglink.server.core.delivery.mcp.server.McpToolRegistrar
import com.felix.livinglink.server.session.delivery.mcp.dto.UserMcpDto
import com.felix.livinglink.server.user.domain.UserDirectory
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

                The response includes known custom calendar event category labels.
                Prefer reusing existing ones for consistency, but feel free to create new ones when it makes sense.
                Keep them in mind internally and do not display them to the user unless asked.
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
