package com.felix.livinglink.calendar.delivery.mcp.dto

import com.felix.livinglink.calendar.domain.EventSpan
import com.felix.livinglink.core.delivery.mcp.dsl.parseInstant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed interface EventSpanMcpDto {
    fun toDomain(): EventSpan

    @Serializable
    @SerialName("timed")
    data class Timed(
        val start: String,
        val end: String,
    ) : EventSpanMcpDto {
        override fun toDomain(): EventSpan =
            EventSpan.Timed(
                start = parseInstant("span.start", start),
                end = parseInstant("span.end", end),
            )
    }

    @Serializable
    @SerialName("allDay")
    data class AllDay(
        val start: String,
        val end: String,
    ) : EventSpanMcpDto {
        override fun toDomain(): EventSpan =
            EventSpan.AllDay(
                start = parseInstant("span.start", start),
                end = parseInstant("span.end", end),
            )
    }

    companion object {
        fun fromDomain(span: EventSpan): EventSpanMcpDto =
            when (span) {
                is EventSpan.Timed ->
                    Timed(
                        start = span.start.toString(),
                        end = span.end.toString(),
                    )
                is EventSpan.AllDay ->
                    AllDay(
                        start = span.start.toString(),
                        end = span.end.toString(),
                    )
            }
    }
}
