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

    override suspend fun findById(id: String): TDomain? =
        storageRepository
            .findById(id)
            ?.let(toDomain)

    override suspend fun <TResponse> updateWithOptimisticLocking(
        id: String,
        modify: (TDomain) -> CrudRepository.UpdateOperationResult<TDomain, TResponse>,
    ): CrudRepository.UpdateResult<TDomain, TResponse> {
        val storageResult =
            storageRepository.updateWithOptimisticLocking(id) { storageCurrent ->
                val domainCurrent = toDomain(storageCurrent)
                when (val operation = modify(domainCurrent)) {
                    is CrudRepository.UpdateOperationResult.NoUpdate ->
                        CrudRepository.UpdateOperationResult.NoUpdate(operation.response)

                    is CrudRepository.UpdateOperationResult.Updated ->
                        CrudRepository.UpdateOperationResult.Updated(
                            newEntity = toStorage(operation.newEntity),
                            response = operation.response,
                        )
                }
            }

        return when (storageResult) {
            is CrudRepository.UpdateResult.NotFound ->
                CrudRepository.UpdateResult.NotFound

            is CrudRepository.UpdateResult.NotUpdated ->
                CrudRepository.UpdateResult.NotUpdated(storageResult.response)

            is CrudRepository.UpdateResult.Updated ->
                CrudRepository.UpdateResult.Updated(
                    newEntity = toDomain(storageResult.newEntity),
                    response = storageResult.response,
                )
        }
    }
}
