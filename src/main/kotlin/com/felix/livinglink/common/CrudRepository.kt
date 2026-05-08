package com.felix.livinglink.common

interface CrudRepository<T> {
    suspend fun create(entity: T): T

    suspend fun update(entity: T): T?

    suspend fun findById(id: String): T?

    suspend fun findAll(): List<T>
}
