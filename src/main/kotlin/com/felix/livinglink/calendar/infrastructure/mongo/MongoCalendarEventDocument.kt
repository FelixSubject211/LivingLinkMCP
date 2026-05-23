package com.felix.livinglink.calendar.infrastructure.mongo

import com.felix.livinglink.calendar.domain.CalendarEvent
import com.felix.livinglink.calendar.domain.EventCategory
import com.felix.livinglink.calendar.domain.EventSpan
import com.felix.livinglink.calendar.domain.RecurrenceRule
import com.felix.livinglink.core.infrastructure.mongo.MongoVersionedDocument
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import kotlin.time.Instant
import com.felix.livinglink.calendar.domain.Participant as DomainParticipant
import com.felix.livinglink.calendar.infrastructure.mongo.MongoCalendarEventFields as Fields

data class MongoCalendarEventDocument(
    @param:BsonId
    override val id: String,
    @param:BsonProperty(Fields.TITLE)
    val title: String,
    @param:BsonProperty(Fields.DESCRIPTION)
    val description: String?,
    @param:BsonProperty(Fields.CREATED_BY_USER_ID)
    val createdByUserId: String,
    @param:BsonProperty(Fields.SPAN)
    val span: Span,
    @param:BsonProperty(Fields.RECURRENCE)
    val recurrence: Recurrence?,
    @param:BsonProperty(Fields.PARTICIPANTS)
    val participants: List<Participant>,
    @param:BsonProperty(Fields.CATEGORY)
    val category: Category,
    @param:BsonProperty(Fields.EFFECTIVE_FROM)
    val effectiveFrom: Instant,
    @param:BsonProperty(Fields.EFFECTIVE_TO)
    val effectiveTo: Instant,
    @param:BsonProperty(Fields.CREATED_AT)
    val createdAt: Instant,
    @param:BsonProperty(Fields.UPDATED_AT)
    val updatedAt: Instant,
    @param:BsonProperty(Fields.VERSION)
    override val version: Long,
) : MongoVersionedDocument<MongoCalendarEventDocument> {
    override fun withVersion(version: Long): MongoCalendarEventDocument =
        copy(version = version)

    fun toDomain(): CalendarEvent =
        CalendarEvent(
            id = id,
            title = title,
            description = description,
            createdByUserId = createdByUserId,
            span = span.toDomain(),
            recurrence = recurrence?.toDomain(),
            participants = participants.map { it.toDomain() },
            category = category.toDomain(),
            createdAt = createdAt,
            updatedAt = updatedAt,
            version = version,
        )

    data class Span(
        @param:BsonProperty(Fields.Span.TYPE)
        val type: String,
        @param:BsonProperty(Fields.Span.START)
        val start: Instant,
        @param:BsonProperty(Fields.Span.END)
        val end: Instant,
    ) {
        fun toDomain(): EventSpan =
            when (type) {
                TYPE_TIMED -> EventSpan.Timed(start = start, end = end)
                TYPE_ALL_DAY -> EventSpan.AllDay(start = start, end = end)
                else -> error("Unknown Span.type: $type")
            }

        companion object {
            const val TYPE_TIMED = "Timed"
            const val TYPE_ALL_DAY = "AllDay"

            fun fromDomain(span: EventSpan): Span =
                when (span) {
                    is EventSpan.Timed -> Span(type = TYPE_TIMED, start = span.start, end = span.end)
                    is EventSpan.AllDay -> Span(type = TYPE_ALL_DAY, start = span.start, end = span.end)
                }
        }
    }

    data class Recurrence(
        @param:BsonProperty(Fields.Recurrence.FREQUENCY)
        val frequency: String,
        @param:BsonProperty(Fields.Recurrence.INTERVAL)
        val interval: Int,
        @param:BsonProperty(Fields.Recurrence.END)
        val end: End,
    ) {
        fun toDomain(): RecurrenceRule =
            RecurrenceRule(
                frequency = RecurrenceRule.Frequency.valueOf(frequency),
                interval = interval,
                end = end.toDomain(),
            )

        data class End(
            @param:BsonProperty(Fields.Recurrence.End.TYPE)
            val type: String,
            @param:BsonProperty(Fields.Recurrence.End.AT)
            val at: Instant?,
            @param:BsonProperty(Fields.Recurrence.End.OCCURRENCES)
            val occurrences: Int?,
        ) {
            fun toDomain(): RecurrenceRule.RecurrenceEnd =
                when (type) {
                    TYPE_NEVER ->
                        RecurrenceRule.RecurrenceEnd.Never

                    TYPE_UNTIL ->
                        RecurrenceRule.RecurrenceEnd.Until(
                            at = requireNotNull(at) { "Recurrence.End.at missing for Until" },
                        )

                    TYPE_COUNT ->
                        RecurrenceRule.RecurrenceEnd.Count(
                            occurrences = requireNotNull(occurrences) { "Recurrence.End.occurrences missing for Count" },
                        )

                    else ->
                        error("Unknown Recurrence.End.type: $type")
                }

            companion object {
                const val TYPE_NEVER = "Never"
                const val TYPE_UNTIL = "Until"
                const val TYPE_COUNT = "Count"

                fun fromDomain(end: RecurrenceRule.RecurrenceEnd): End =
                    when (end) {
                        is RecurrenceRule.RecurrenceEnd.Never ->
                            End(type = TYPE_NEVER, at = null, occurrences = null)

                        is RecurrenceRule.RecurrenceEnd.Until ->
                            End(type = TYPE_UNTIL, at = end.at, occurrences = null)

                        is RecurrenceRule.RecurrenceEnd.Count ->
                            End(type = TYPE_COUNT, at = null, occurrences = end.occurrences)
                    }
            }
        }

        companion object {
            fun fromDomain(rule: RecurrenceRule): Recurrence =
                Recurrence(
                    frequency = rule.frequency.name,
                    interval = rule.interval,
                    end = End.fromDomain(rule.end),
                )
        }
    }

    data class Participant(
        @param:BsonProperty(Fields.Participant.USER_ID)
        val userId: String,
        @param:BsonProperty(Fields.Participant.RSVP_EVENTS)
        val rsvpEvents: List<RsvpEvent>,
    ) {
        fun toDomain(): DomainParticipant =
            DomainParticipant(
                userId = userId,
                rsvpEvents = rsvpEvents.map { it.toDomain() },
            )

        data class RsvpEvent(
            @param:BsonProperty(Fields.Participant.RsvpEvent.STATUS)
            val status: String,
            @param:BsonProperty(Fields.Participant.RsvpEvent.AT)
            val at: Instant,
        ) {
            fun toDomain(): DomainParticipant.RsvpEvent =
                DomainParticipant.RsvpEvent(
                    status = DomainParticipant.RsvpStatus.valueOf(status),
                    at = at,
                )

            companion object {
                fun fromDomain(event: DomainParticipant.RsvpEvent): RsvpEvent =
                    RsvpEvent(
                        status = event.status.name,
                        at = event.at,
                    )
            }
        }

        companion object {
            fun fromDomain(participant: DomainParticipant): Participant =
                Participant(
                    userId = participant.userId,
                    rsvpEvents = participant.rsvpEvents.map { RsvpEvent.fromDomain(it) },
                )
        }
    }

    data class Category(
        @param:BsonProperty(Fields.Category.TYPE)
        val type: String,
        @param:BsonProperty(Fields.Category.LABEL)
        val label: String?,
        @param:BsonProperty(Fields.Category.SHOPPING_LIST_ITEM_IDS)
        val shoppingListItemIds: List<String>?,
    ) {
        fun toDomain(): EventCategory =
            when (type) {
                TYPE_NONE ->
                    EventCategory.None

                TYPE_CUSTOM ->
                    EventCategory.Custom(
                        label = requireNotNull(label) { "Category.label missing for Custom" },
                    )

                TYPE_SHOPPING ->
                    EventCategory.Shopping(
                        shoppingListItemIds = shoppingListItemIds ?: emptyList(),
                    )

                else ->
                    error("Unknown Category.type: $type")
            }

        companion object {
            const val TYPE_NONE = "None"
            const val TYPE_CUSTOM = "Custom"
            const val TYPE_SHOPPING = "Shopping"

            fun fromDomain(category: EventCategory): Category =
                when (category) {
                    is EventCategory.None ->
                        Category(type = TYPE_NONE, label = null, shoppingListItemIds = null)

                    is EventCategory.Custom ->
                        Category(type = TYPE_CUSTOM, label = category.label, shoppingListItemIds = null)

                    is EventCategory.Shopping ->
                        Category(type = TYPE_SHOPPING, label = null, shoppingListItemIds = category.shoppingListItemIds)
                }
        }
    }

    companion object {
        fun fromDomain(event: CalendarEvent): MongoCalendarEventDocument {
            val span = Span.fromDomain(event.span)
            val recurrence = event.recurrence?.let { Recurrence.fromDomain(it) }

            return MongoCalendarEventDocument(
                id = event.id,
                title = event.title,
                description = event.description,
                createdByUserId = event.createdByUserId,
                span = span,
                recurrence = recurrence,
                participants = event.participants.map { Participant.fromDomain(it) },
                category = Category.fromDomain(event.category),
                effectiveFrom = span.start,
                effectiveTo = effectiveTo(span = span, recurrence = recurrence),
                createdAt = event.createdAt,
                updatedAt = event.updatedAt,
                version = event.version,
            )
        }

        private fun effectiveTo(
            span: Span,
            recurrence: Recurrence?,
        ): Instant {
            val end = recurrence?.end ?: return span.end
            return when (end.type) {
                Recurrence.End.TYPE_NEVER -> Instant.DISTANT_FUTURE
                Recurrence.End.TYPE_UNTIL -> requireNotNull(end.at) { "End.at missing for Until" }
                Recurrence.End.TYPE_COUNT -> Instant.DISTANT_FUTURE
                else -> error("Unknown Recurrence.End.type: ${end.type}")
            }
        }
    }
}
