package com.felix.livinglink.user.domain

interface UserLookup {
    suspend fun findByIds(ids: List<String>): Map<String, User>
}
