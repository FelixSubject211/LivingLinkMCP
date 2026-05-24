package com.felix.livinglink.core.delivery.mcp.dsl

import com.felix.livinglink.core.config.TimezoneSettings
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

fun Instant.toMcpString(timezoneSettings: TimezoneSettings): String {
    val zoned =
        ZonedDateTime.ofInstant(
            this.toJavaInstant(),
            ZoneId.of(timezoneSettings.timezoneId),
        )
    val formatted =
        zoned.format(
            java.time.format.DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss"),
        )
    return "$formatted (${timezoneSettings.timezoneId})"
}

fun parseInstant(name: String, value: String): Instant {
    val trimmed = value.trim()

    tryParse(trimmed) { Instant.parse(it) }?.let { return it }
    tryParse(trimmed) { OffsetDateTime.parse(it).toInstant().toKotlinInstant() }?.let { return it }
    tryParse(trimmed) {
        LocalDate
            .parse(it)
            .atStartOfDay()
            .atOffset(java.time.ZoneOffset.UTC)
            .toInstant()
            .toKotlinInstant()
    }?.let { return it }

    throw IllegalArgumentException(
        "'$name' must be a valid date or instant. " +
            "Accepted formats: '2026-05-24', " +
            "'2026-05-24T10:00:00+02:00', '2026-05-24T10:00:00Z'. " +
            "Got: '$value'.",
    )
}

private fun tryParse(value: String, parser: (String) -> Instant): Instant? =
    try {
        parser(value)
    } catch (_: Exception) {
        null
    }
