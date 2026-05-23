package com.felix.livinglink.user.domain

interface UserDirectory {
    suspend fun all(): List<User>
}
