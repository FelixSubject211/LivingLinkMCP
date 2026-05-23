package com.felix.livinglink.calendar.application

import com.felix.livinglink.calendar.domain.CalendarEventQuery
import com.felix.livinglink.calendar.domain.CalendarEventRepository
import com.felix.livinglink.calendar.domain.CalendarEventSort
import com.felix.livinglink.calendar.domain.EventSpan
import com.felix.livinglink.calendar.domain.ScheduledEvent
import com.felix.livinglink.calendar.domain.ScheduledEventCalculator
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
                    )
                }.sortedWith(comparatorFor(input.sort))

        return Output(scheduledEvents = scheduled)
    }

    private fun comparatorFor(sort: CalendarEventSort): Comparator<ScheduledEvent> =
        when (sort) {
            CalendarEventSort.EffectiveStartAscending ->
                compareBy { it.span.start }

            CalendarEventSort.EffectiveStartDescending ->
                compareByDescending { it.span.start }

            CalendarEventSort.CreatedAtAscending ->
                compareBy { it.createdAt }

            CalendarEventSort.CreatedAtDescending ->
                compareByDescending { it.createdAt }
        }

    private val EventSpan.start: Instant
        get() =
            when (this) {
                is EventSpan.Timed -> start
                is EventSpan.AllDay -> start
            }

    data class Input(
        val query: CalendarEventQuery,
        val sort: CalendarEventSort,
    )

    data class Output(
        val scheduledEvents: List<ScheduledEvent>,
    )
}
