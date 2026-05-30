package com.felix.livinglink.calendar.application

import com.felix.livinglink.calendar.domain.CalendarEventRepository
import org.koin.core.annotation.Single

@Single
class GetEventCategoriesUseCase(
    private val calendarEventRepository: CalendarEventRepository,
) {
    suspend operator fun invoke(): Output {
        val knownCustomLabels = calendarEventRepository.findDistinctCustomCategoryLabels()

        return Output(knownCustomLabels = knownCustomLabels)
    }

    data class Output(
        val knownCustomLabels: List<String>,
    )
}
