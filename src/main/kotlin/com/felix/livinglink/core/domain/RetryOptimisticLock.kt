package com.felix.livinglink.core.domain

suspend fun retryOptimisticLock(
    maxAttempts: Int = 100,
    block: suspend () -> Unit,
) {
    require(maxAttempts > 0) {
        "maxAttempts must be greater than 0."
    }

    repeat(maxAttempts - 1) {
        try {
            block()
            return
        } catch (exception: OptimisticLockException) {
            // Retry.
        }
    }

    block()
}
