package com.felix.livinglink.calendar.application

import com.felix.livinglink.calendar.domain.CalendarEventQuery
import com.felix.livinglink.calendar.domain.CalendarEventRepository
import com.felix.livinglink.calendar.domain.CalendarEventSort
import com.felix.livinglink.calendar.domain.EventSpan
import com.felix.livinglink.calendar.domain.ScheduledEvent
import com.felix.livinglink.calendar.domain.ScheduledEventCalculator
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import org.koin.core.annotation.Single
import kotlin.time.Instant

@Single
class ListCalendarEventsUseCase(
    private val calendarEventRepository: CalendarEventRepository,
    private val scheduledEventCalculator: ScheduledEventCalculator,
) {
    suspend operator fun invoke(input: Input): Output {
        val events = calendarEventRepository.find(input.query)

        val scheduled =
            events
                .flatMap { event ->
                    scheduledEventCalculator.calculate(
                        event = event,
                        from = input.query.from,
                        to = input.query.to,
                        timeZone = input.timeZone,
                    )
                }.sortedWith(comparatorFor(input.sort, input.timeZone))

        return Output(scheduledEvents = scheduled)
    }

    private fun comparatorFor(
        sort: CalendarEventSort,
        timeZone: TimeZone,
    ): Comparator<ScheduledEvent> =
        when (sort) {
            CalendarEventSort.EffectiveStartAscending ->
                compareBy { it.span.startInstant(timeZone) }

            CalendarEventSort.EffectiveStartDescending ->
                compareByDescending { it.span.startInstant(timeZone) }

            CalendarEventSort.CreatedAtAscending ->
                compareBy { it.createdAt }

            CalendarEventSort.CreatedAtDescending ->
                compareByDescending { it.createdAt }
        }

    private fun EventSpan.startInstant(timeZone: TimeZone): Instant =
        when (this) {
            is EventSpan.Timed -> start
            is EventSpan.AllDay -> startDate.atStartOfDayIn(timeZone)
        }

    data class Input(
        val query: CalendarEventQuery,
        val sort: CalendarEventSort,
        val timeZone: TimeZone,
    )

    data class Output(
        val scheduledEvents: List<ScheduledEvent>,
    )
}
