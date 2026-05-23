package com.felix.livinglink.calendar.domain

import com.felix.livinglink.core.domain.CrudRepository

interface CalendarEventRepository : CrudRepository<CalendarEvent> {
    suspend fun find(query: CalendarEventQuery): List<CalendarEvent>
}
