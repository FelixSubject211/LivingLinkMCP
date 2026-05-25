package com.felix.livinglink.shoppingList.application

import com.felix.livinglink.core.domain.TimeProvider
import com.felix.livinglink.core.domain.stubConflict
import com.felix.livinglink.core.domain.stubDoesNotUpdate
import com.felix.livinglink.core.domain.stubNotFound
import com.felix.livinglink.core.domain.stubUpdates
import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import com.felix.livinglink.shoppingList.domain.ShoppingListItemRepository
import com.felix.livinglink.shoppingList.domain.completionEvent
import com.felix.livinglink.shoppingList.domain.shoppingListItem
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

class CompleteShoppingListItemsUseCaseTest {
    private val repository = mock<ShoppingListItemRepository>()
    private val timeProvider = mock<TimeProvider>()

    private val useCase =
        CompleteShoppingListItemsUseCase(
            shoppingListItemRepository = repository,
            timeProvider = timeProvider,
        )

    private val now = Clock.System.now()

    private val openItem =
        shoppingListItem(
            id = "id-1",
            createdAt = now - 1.days,
        )

    private val completedItem =
        openItem.copy(
            completionEvents =
                listOf(
                    completionEvent(at = now - 1.hours),
                ),
        )

    @Test
    fun `completed items land in completedItems`() =
        runTest {
            // given
            every { timeProvider() } returns now
            repository.stubUpdates(id = "id-1", currentItem = openItem)

            val expectedCompleted = openItem.complete(byUserId = "user-1", at = now)

            // when
            val result =
                useCase(
                    CompleteShoppingListItemsUseCase.Input(
                        byUserId = "user-1",
                        ids = setOf("id-1"),
                    ),
                )

            // then
            assertEquals(
                CompleteShoppingListItemsUseCase.Output(
                    completedItems = listOf(expectedCompleted),
                    alreadyCompletedItems = emptyList(),
                    missingIds = emptyList(),
                    conflictedIds = emptyList(),
                ),
                result,
            )
        }

    @Test
    fun `already completed items land in alreadyCompletedItems`() =
        runTest {
            // given
            every { timeProvider() } returns now
            repository.stubDoesNotUpdate(id = "id-1", currentItem = completedItem)

            // when
            val result =
                useCase(
                    CompleteShoppingListItemsUseCase.Input(
                        byUserId = "user-1",
                        ids = setOf("id-1"),
                    ),
                )

            // then
            assertEquals(
                CompleteShoppingListItemsUseCase.Output(
                    completedItems = emptyList(),
                    alreadyCompletedItems = listOf(completedItem),
                    missingIds = emptyList(),
                    conflictedIds = emptyList(),
                ),
                result,
            )
        }

    @Test
    fun `missing items land in missingIds`() =
        runTest {
            // given
            repository.stubNotFound<ShoppingListItem>(id = "id-1")

            // when
            val result =
                useCase(
                    CompleteShoppingListItemsUseCase.Input(
                        byUserId = "user-1",
                        ids = setOf("id-1"),
                    ),
                )

            // then
            assertEquals(
                CompleteShoppingListItemsUseCase.Output(
                    completedItems = emptyList(),
                    alreadyCompletedItems = emptyList(),
                    missingIds = listOf("id-1"),
                    conflictedIds = emptyList(),
                ),
                result,
            )
        }

    @Test
    fun `conflicted items land in conflictedIds`() =
        runTest {
            // given
            repository.stubConflict<ShoppingListItem>(id = "id-1")

            // when
            val result =
                useCase(
                    CompleteShoppingListItemsUseCase.Input(
                        byUserId = "user-1",
                        ids = setOf("id-1"),
                    ),
                )

            // then
            assertEquals(
                CompleteShoppingListItemsUseCase.Output(
                    completedItems = emptyList(),
                    alreadyCompletedItems = emptyList(),
                    missingIds = emptyList(),
                    conflictedIds = listOf("id-1"),
                ),
                result,
            )
        }
}
