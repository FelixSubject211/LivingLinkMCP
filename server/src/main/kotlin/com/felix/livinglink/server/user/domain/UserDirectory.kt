package com.felix.livinglink.server.user.domain

interface UserDirectory {
    suspend fun all(): List<User>
}
