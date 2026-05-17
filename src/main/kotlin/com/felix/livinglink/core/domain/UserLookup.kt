package com.felix.livinglink.core.domain

interface UserLookup {
    suspend fun findByIds(ids: List<String>): Map<String, User>
}
