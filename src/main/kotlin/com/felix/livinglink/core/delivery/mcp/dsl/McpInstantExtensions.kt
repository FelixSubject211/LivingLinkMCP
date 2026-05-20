package com.felix.livinglink.core.delivery.mcp.dsl

import com.felix.livinglink.core.system.TimezoneSettings
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant

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
