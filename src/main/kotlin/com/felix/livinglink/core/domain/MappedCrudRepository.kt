package com.felix.livinglink.core.domain

class MappedCrudRepository<TDomain, TStorage>(
    private val storageRepository: CrudRepository<TStorage>,
    private val toStorage: (TDomain) -> TStorage,
    private val toDomain: (TStorage) -> TDomain,
) : CrudRepository<TDomain> {
    override suspend fun create(value: TDomain): TDomain =
        storageRepository
            .create(toStorage(value))
            .let(toDomain)

    override suspend fun update(value: TDomain): TDomain? =
        storageRepository
            .update(toStorage(value))
            ?.let(toDomain)

    override suspend fun findById(id: String): TDomain? =
        storageRepository
            .findById(id)
            ?.let(toDomain)

    override suspend fun findAll(): List<TDomain> =
        storageRepository
            .findAll()
            .map(toDomain)
}
