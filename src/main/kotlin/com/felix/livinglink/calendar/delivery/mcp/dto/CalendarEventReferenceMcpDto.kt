package com.felix.livinglink.calendar.delivery.mcp.dto

import com.felix.livinglink.calendar.domain.CalendarEvent
import kotlinx.serialization.Serializable

@Serializable
data class CalendarEventReferenceMcpDto(
    val id: String,
    val title: String,
)

fun CalendarEvent.toMcpReferenceDto(): CalendarEventReferenceMcpDto =
    CalendarEventReferenceMcpDto(
        id = id,
        title = title,
    )
