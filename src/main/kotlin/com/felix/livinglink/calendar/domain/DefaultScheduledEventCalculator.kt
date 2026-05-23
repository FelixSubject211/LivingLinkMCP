package com.felix.livinglink.calendar.domain

import com.felix.livinglink.calendar.domain.RecurrenceRule.Frequency
import com.felix.livinglink.calendar.domain.RecurrenceRule.RecurrenceEnd
import org.koin.core.annotation.Single
import java.time.ZoneOffset
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

@Single(binds = [ScheduledEventCalculator::class])
class DefaultScheduledEventCalculator : ScheduledEventCalculator {
    override fun calculate(
        event: CalendarEvent,
        from: Instant,
        to: Instant,
    ): List<ScheduledEvent> {
        require(to >= from) { "to must be >= from" }

        return when (event.recurrence) {
            null -> calculateSingle(event, from, to)
            else -> calculateRecurring(event, event.recurrence, from, to)
        }
    }

    private fun calculateSingle(
        event: CalendarEvent,
        from: Instant,
        to: Instant,
    ): List<ScheduledEvent> {
        val span = event.span
        if (!span.intersects(from, to)) {
            return emptyList()
        }
        return listOf(event.toScheduled(span))
    }

    private fun calculateRecurring(
        event: CalendarEvent,
        recurrence: RecurrenceRule,
        from: Instant,
        to: Instant,
    ): List<ScheduledEvent> {
        val baseSpan = event.span
        val baseDurationMillis = baseSpan.end.toEpochMilliseconds() - baseSpan.start.toEpochMilliseconds()

        return generateSequence(0) { it + 1 }
            .takeWhile { index -> recurrence.hasOccurrence(index) }
            .map { index -> baseSpan.shiftedBy(recurrence, index, baseDurationMillis) }
            .takeWhile { span -> !span.startsAfter(to) && !span.startsAfter(recurrence.end) }
            .filter { span -> span.intersects(from, to) }
            .map { span -> event.toScheduled(span) }
            .toList()
    }

    private fun RecurrenceRule.hasOccurrence(index: Int): Boolean =
        when (val end = end) {
            is RecurrenceEnd.Count -> index < end.occurrences
            is RecurrenceEnd.Never, is RecurrenceEnd.Until -> true
        }

    private fun EventSpan.shiftedBy(
        recurrence: RecurrenceRule,
        index: Int,
        durationMillis: Long,
    ): EventSpan {
        val newStart = start.plus(recurrence.frequency, recurrence.interval * index)
        val newEnd = Instant.fromEpochMilliseconds(newStart.toEpochMilliseconds() + durationMillis)
        return withRange(newStart, newEnd)
    }

    private fun EventSpan.intersects(from: Instant, to: Instant): Boolean =
        end >= from && start <= to

    private fun EventSpan.startsAfter(instant: Instant): Boolean =
        start > instant

    private fun EventSpan.startsAfter(end: RecurrenceEnd): Boolean =
        end is RecurrenceEnd.Until && startsAfter(end.at)

    private fun Instant.plus(frequency: Frequency, steps: Int): Instant {
        if (steps == 0) return this
        val zoned = toJavaInstant().atOffset(ZoneOffset.UTC)
        val shifted =
            when (frequency) {
                Frequency.Daily -> zoned.plusDays(steps.toLong())
                Frequency.Weekly -> zoned.plusWeeks(steps.toLong())
                Frequency.Monthly -> zoned.plusMonths(steps.toLong())
                Frequency.Yearly -> zoned.plusYears(steps.toLong())
            }
        return shifted.toInstant().toKotlinInstant()
    }

    private val EventSpan.start: Instant
        get() =
            when (this) {
                is EventSpan.Timed -> start
                is EventSpan.AllDay -> start
            }

    private val EventSpan.end: Instant
        get() =
            when (this) {
                is EventSpan.Timed -> end
                is EventSpan.AllDay -> end
            }

    private fun EventSpan.withRange(start: Instant, end: Instant): EventSpan =
        when (this) {
            is EventSpan.Timed -> EventSpan.Timed(start = start, end = end)
            is EventSpan.AllDay -> EventSpan.AllDay(start = start, end = end)
        }

    private fun CalendarEvent.toScheduled(span: EventSpan): ScheduledEvent =
        ScheduledEvent(
            sourceEventId = id,
            title = title,
            description = description,
            createdByUserId = createdByUserId,
            span = span,
            participants = participants,
            category = category,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
}
