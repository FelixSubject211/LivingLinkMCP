package com.felix.livinglink.calendar.domain

import kotlin.time.Instant

sealed interface EventSpan {
    data class Timed(
        val start: Instant,
        val end: Instant,
    ) : EventSpan {
        init {
            require(end >= start) { "Timed.end must be >= start" }
        }
    }

    data class AllDay(
        val start: Instant,
        val end: Instant,
    ) : EventSpan {
        init {
            require(end >= start) { "AllDay.end must be >= start" }
        }
    }
}
