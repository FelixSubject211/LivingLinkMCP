package com.felix.livinglink.core.config

import org.koin.core.annotation.Single

@Single
class TimezoneSettings {
    val timezoneId: String = Env.required("LIVINGLINK_TIMEZONE")
}
