package com.felix.livinglink.user.domain

interface UserLookup {
    suspend fun findByIds(ids: Set<String>): Map<String, User>
}
