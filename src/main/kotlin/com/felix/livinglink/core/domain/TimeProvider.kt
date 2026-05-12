package com.felix.livinglink.core.domain

import kotlin.time.Instant

interface TimeProvider {
    operator fun invoke(): Instant
}
