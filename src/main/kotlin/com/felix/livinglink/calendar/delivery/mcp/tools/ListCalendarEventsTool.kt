package com.felix.livinglink.calendar.delivery.mcp.tools

import com.felix.livinglink.calendar.application.ListCalendarEventsUseCase
import com.felix.livinglink.calendar.delivery.mcp.dto.CalendarEventSortMcpDto
import com.felix.livinglink.calendar.delivery.mcp.dto.ScheduledEventDetailMcpDto
import com.felix.livinglink.calendar.delivery.mcp.dto.toMcpDetailDto
import com.felix.livinglink.calendar.domain.CalendarEventQuery
import com.felix.livinglink.core.config.McpRequestUser
import com.felix.livinglink.core.config.TimezoneSettings
import com.felix.livinglink.core.delivery.mcp.dsl.McpToolDsl.tool
import com.felix.livinglink.core.delivery.mcp.dsl.success
import com.felix.livinglink.core.delivery.mcp.server.McpToolRegistrar
import com.felix.livinglink.user.delivery.mcp.resolveUsers
import com.felix.livinglink.user.domain.UserLookup
import io.modelcontextprotocol.kotlin.sdk.server.Server
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single
import kotlin.time.Instant

@Single(binds = [McpToolRegistrar::class])
class ListCalendarEventsTool(
    private val listCalendarEventsUseCase: ListCalendarEventsUseCase,
    private val userLookup: UserLookup,
    private val timezoneSettings: TimezoneSettings,
) : McpToolRegistrar {
    override fun register(
        server: Server,
        user: McpRequestUser,
    ) {
        server.tool(
            name = "list_calendar_events",
            description = "Lists calendar events that intersect a time range. Recurring events are expanded into individual occurrences.",
        ) {
            val from =
                required<String>(
                    name = "from",
                    description = "Start of the time range as ISO 8601 instant.",
                )

            val to =
                required<String>(
                    name = "to",
                    description = "End of the time range as ISO 8601 instant.",
                )

            val participantUserIds =
                optional<List<String>>(
                    name = "participantUserIds",
                    description = "Filter: only events with at least one of these participants. Use IDs from the 'Known users' list in the server instructions.",
                )

            val createdByUserIds =
                optional<List<String>>(
                    name = "createdByUserIds",
                    description = "Filter: only events created by one of these users. Use IDs from the 'Known users' list in the server instructions.",
                )

            val sort =
                optional<CalendarEventSortMcpDto>(
                    name = "sort",
                    description = "Sort order.",
                    default = CalendarEventSortMcpDto.EffectiveStartAscending,
                )

            handle {
                val output =
                    listCalendarEventsUseCase(
                        ListCalendarEventsUseCase.Input(
                            query =
                                CalendarEventQuery(
                                    from = parseRangeInstant("from", from()),
                                    to = parseRangeInstant("to", to()),
                                    participantUserIds = participantUserIds()?.toSet(),
                                    createdByUserIds = createdByUserIds()?.toSet(),
                                ),
                            sort = sort().toDomain(),
                        ),
                    )

                val resolvedUsers =
                    resolveUsers(
                        userLookup = userLookup,
                        ids = output.scheduledEvents.flatMap { it.referencedUserIds },
                    )

                success(
                    Output(
                        scheduledEvents =
                            output.scheduledEvents.map { event ->
                                event.toMcpDetailDto(
                                    resolvedUsers = resolvedUsers,
                                    timezoneSettings = timezoneSettings,
                                )
                            },
                    ),
                )
            }
        }
    }

    // TODO add to DSL
    private fun parseRangeInstant(name: String, value: String): Instant =
        runCatching { Instant.parse(value) }
            .getOrElse {
                throw IllegalArgumentException(
                    "'$name' must be a valid ISO 8601 instant, was '$value'.",
                )
            }

    @Serializable
    private data class Output(
        val scheduledEvents: List<ScheduledEventDetailMcpDto>,
    )
}
