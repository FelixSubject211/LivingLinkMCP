package com.felix.livinglink.core.system

import com.felix.livinglink.core.domain.TimeProvider
import org.koin.core.annotation.Single
import kotlin.time.Clock
import kotlin.time.Instant

@Single(binds = [TimeProvider::class])
class KotlinTimeProvider : TimeProvider {
    override fun invoke(): Instant =
        Clock.System.now()
}
