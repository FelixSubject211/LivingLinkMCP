package com.felix.livinglink.core

import org.koin.core.annotation.Single
import kotlin.time.Clock
import kotlin.time.Instant

interface TimeProvider {
    operator fun invoke(): Instant
}

@Single(binds = [TimeProvider::class])
class KotlinTimeProvider : TimeProvider {
    override fun invoke(): Instant =
        Clock.System.now()
}
