package com.felix.livinglink.calendar.domain

import kotlin.time.Instant

interface ScheduledEventCalculator {
    fun calculate(
        event: CalendarEvent,
        from: Instant,
        to: Instant,
    ): List<ScheduledEvent>
}
