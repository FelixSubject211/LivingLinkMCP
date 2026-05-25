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
import com.felix.livinglink.user.delivery.mcp.KnownUsersDescriptionProvider
import com.felix.livinglink.user.delivery.mcp.resolveUsers
import com.felix.livinglink.user.domain.UserLookup
import io.modelcontextprotocol.kotlin.sdk.server.Server
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single

@Single(binds = [McpToolRegistrar::class])
class ListCalendarEventsTool(
    private val listCalendarEventsUseCase: ListCalendarEventsUseCase,
    private val userLookup: UserLookup,
    private val timezoneSettings: TimezoneSettings,
    private val knownUsersDescriptionProvider: KnownUsersDescriptionProvider,
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
                requiredInstant(
                    name = "from",
                    description = "Start of the time range as ISO 8601 instant.",
                )

            val to =
                requiredInstant(
                    name = "to",
                    description = "End of the time range as ISO 8601 instant.",
                )

            val participantUserIds =
                optional<List<String>>(
                    name = "participantUserIds",
                    description =
                        knownUsersDescriptionProvider.describeWith(
                            "Filter: only events with at least one of these participants.",
                        ),
                )

            val createdByUserIds =
                optional<List<String>>(
                    name = "createdByUserIds",
                    description =
                        knownUsersDescriptionProvider.describeWith(
                            "Filter: only events created by one of these users.",
                        ),
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
                                    from = from(),
                                    to = to(),
                                    participantUserIds = participantUserIds()?.toSet(),
                                    createdByUserIds = createdByUserIds()?.toSet(),
                                ),
                            sort = sort().toDomain(),
                            timeZone = timezoneSettings.timeZone,
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

    @Serializable
    private data class Output(
        val scheduledEvents: List<ScheduledEventDetailMcpDto>,
    )
}
