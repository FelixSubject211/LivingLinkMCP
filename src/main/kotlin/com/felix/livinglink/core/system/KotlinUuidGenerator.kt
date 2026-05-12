package com.felix.livinglink.core.system

import com.felix.livinglink.core.domain.UuidGenerator
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Single(binds = [UuidGenerator::class])
class KotlinUuidGenerator : UuidGenerator {
    @OptIn(ExperimentalUuidApi::class)
    override fun invoke(): String =
        Uuid.random().toString()
}
