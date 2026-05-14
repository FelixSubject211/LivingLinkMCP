package com.felix.livinglink.core.domain

interface UserLookup {
    suspend fun findById(id: String): User?
}
