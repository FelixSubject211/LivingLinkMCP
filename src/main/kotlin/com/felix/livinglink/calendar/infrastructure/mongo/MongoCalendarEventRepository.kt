package com.felix.livinglink.calendar.infrastructure.mongo

import com.felix.livinglink.calendar.domain.CalendarEvent
import com.felix.livinglink.calendar.domain.CalendarEventQuery
import com.felix.livinglink.calendar.domain.CalendarEventRepository
import com.felix.livinglink.core.domain.CrudRepository
import com.felix.livinglink.core.domain.MappedCrudRepository
import com.felix.livinglink.core.infrastructure.mongo.MongoCrudRepository
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.bson.conversions.Bson
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import com.felix.livinglink.calendar.infrastructure.mongo.MongoCalendarEventFields as Fields

@Single(binds = [CalendarEventRepository::class])
class MongoCalendarEventRepository(
    @Named("calendarEvents")
    private val collection: MongoCollection<MongoCalendarEventDocument>,
) : CalendarEventRepository,
    CrudRepository<CalendarEvent> by MappedCrudRepository(
        storageRepository =
            MongoCrudRepository(
                collection = collection,
                entityName = "Calendar event",
            ),
        toStorage = MongoCalendarEventDocument::fromDomain,
        toDomain = MongoCalendarEventDocument::toDomain,
    ) {
    override suspend fun find(query: CalendarEventQuery): Flow<CalendarEvent> {
        val filters =
            buildList<Bson> {
                add(
                    Filters.and(
                        Filters.lte(Fields.EFFECTIVE_FROM, query.to),
                        Filters.gte(Fields.EFFECTIVE_TO, query.from),
                    ),
                )

                query.participantUserIds?.takeIf { it.isNotEmpty() }?.let { ids ->
                    add(Filters.`in`(Fields.PARTICIPANT_USER_ID_PATH, ids))
                }

                query.createdByUserIds?.takeIf { it.isNotEmpty() }?.let { ids ->
                    add(Filters.`in`(Fields.CREATED_BY_USER_ID, ids))
                }
            }

        return collection
            .find(Filters.and(filters))
            .map { it.toDomain() }
    }
}
