package com.felix.livinglink.core.delivery.mcp.dsl

import com.felix.livinglink.core.domain.OptimisticLockException

fun Throwable.toToolErrorMessage(): String =
    when (this) {
        is IllegalArgumentException -> message ?: "Invalid tool input."
        is OptimisticLockException -> message ?: "The resource was changed concurrently."
        else -> "Tool execution failed."
    }
