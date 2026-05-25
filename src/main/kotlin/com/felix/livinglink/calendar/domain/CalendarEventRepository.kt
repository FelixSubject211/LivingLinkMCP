package com.felix.livinglink.calendar.domain

import com.felix.livinglink.core.domain.CrudRepository
import kotlinx.coroutines.flow.Flow

interface CalendarEventRepository : CrudRepository<CalendarEvent> {
    suspend fun find(query: CalendarEventQuery): Flow<CalendarEvent>
}
