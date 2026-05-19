package com.felix.livinglink.core.delivery.mcp.dsl

import kotlin.time.Instant

fun Instant.toMcpString(): String =
    "$this UTC"
