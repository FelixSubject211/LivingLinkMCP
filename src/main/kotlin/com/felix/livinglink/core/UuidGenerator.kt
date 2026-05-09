package com.felix.livinglink.core

import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface UuidGenerator {
    operator fun invoke(): String
}

@Single(binds = [UuidGenerator::class])
class KotlinUuidGenerator : UuidGenerator {
    @OptIn(ExperimentalUuidApi::class)
    override fun invoke(): String =
        Uuid.random().toString()
}
