package com.felix.livinglink.core.system

import org.koin.core.annotation.Single
import java.util.TimeZone

@Single
class TimezoneSettings {
    val timezoneId: String = Env.required("LIVINGLINK_TIMEZONE")
    val timezone: TimeZone = TimeZone.getTimeZone(timezoneId)
}
