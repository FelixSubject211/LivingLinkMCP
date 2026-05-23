package com.felix.livinglink.calendar.delivery.mcp.dto

import com.felix.livinglink.calendar.domain.ScheduledEvent
import com.felix.livinglink.core.config.TimezoneSettings
import com.felix.livinglink.core.delivery.mcp.dsl.toMcpString
import com.felix.livinglink.user.delivery.mcp.ResolvedUsers
import com.felix.livinglink.user.delivery.mcp.UserReferenceMcpDto
import kotlinx.serialization.Serializable

@Serializable
data class ScheduledEventDetailMcpDto(
    val sourceEventId: String,
    val title: String,
    val description: String?,
    val createdBy: UserReferenceMcpDto,
    val span: EventSpanMcpDto,
    val participants: List<ParticipantMcpDto>,
    val category: EventCategoryMcpDto,
    val createdAt: String,
    val updatedAt: String,
) {
    @Serializable
    data class ParticipantMcpDto(
        val user: UserReferenceMcpDto,
        val currentStatus: RsvpStatusMcpDto,
    )
}

fun ScheduledEvent.toMcpDetailDto(
    resolvedUsers: ResolvedUsers,
    timezoneSettings: TimezoneSettings,
): ScheduledEventDetailMcpDto =
    ScheduledEventDetailMcpDto(
        sourceEventId = sourceEventId,
        title = title,
        description = description,
        createdBy =
            UserReferenceMcpDto.fromResolved(
                id = createdByUserId,
                resolvedUsers = resolvedUsers,
            ),
        span = EventSpanMcpDto.fromDomain(span),
        participants =
            participants.map { participant ->
                ScheduledEventDetailMcpDto.ParticipantMcpDto(
                    user =
                        UserReferenceMcpDto.fromResolved(
                            id = participant.userId,
                            resolvedUsers = resolvedUsers,
                        ),
                    currentStatus = RsvpStatusMcpDto.fromDomain(participant.currentStatus),
                )
            },
        category = EventCategoryMcpDto.fromDomain(category),
        createdAt = createdAt.toMcpString(timezoneSettings),
        updatedAt = updatedAt.toMcpString(timezoneSettings),
    )
