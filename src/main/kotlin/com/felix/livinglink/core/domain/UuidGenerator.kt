package com.felix.livinglink.core.domain

interface UuidGenerator {
    operator fun invoke(): String
}
