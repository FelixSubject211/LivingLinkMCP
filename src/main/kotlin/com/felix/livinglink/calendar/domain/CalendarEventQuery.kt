package com.felix.livinglink.calendar.domain

import kotlin.time.Instant

data class CalendarEventQuery(
    val from: Instant,
    val to: Instant,
    val participantUserIds: Set<String>? = null,
    val createdByUserIds: Set<String>? = null,
) {
    init {
        require(to >= from) { "CalendarEventQuery.to must be >= from" }
    }
}
