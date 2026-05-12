package com.felix.livinglink.core.domain

interface CrudRepository<T> {
    suspend fun create(value: T): T

    suspend fun update(value: T): T?

    suspend fun findById(id: String): T?

    suspend fun findAll(): List<T>
}
