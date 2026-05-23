package com.felix.livinglink.shoppingList.application

import com.felix.livinglink.core.domain.TimeProvider
import com.felix.livinglink.core.domain.UuidGenerator
import com.felix.livinglink.shoppingList.domain.ShoppingListItem
import com.felix.livinglink.shoppingList.domain.ShoppingListItemRepository
import com.felix.livinglink.shoppingList.domain.shoppingListItem
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.sequentially
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

class AddShoppingListItemsUseCaseTest {
    private val repository = mock<ShoppingListItemRepository>()
    private val uuidGenerator = mock<UuidGenerator>()
    private val timeProvider = mock<TimeProvider>()

    private val useCase =
        AddShoppingListItemsUseCase(
            shoppingListItemRepository = repository,
            uuidGenerator = uuidGenerator,
            timeProvider = timeProvider,
        )

    @Test
    fun `creates one item per name with correct mapping`() =
        runTest {
            // given
            every { uuidGenerator() } sequentially {
                returns("id-1")
                returns("id-2")
            }

            val time1 = Clock.System.now()
            val time2 = time1 + 1.seconds

            every { timeProvider() } sequentially {
                returns(time1)
                returns(time2)
            }

            everySuspend { repository.create(any()) } calls { (item: ShoppingListItem) -> item }

            // when
            val result =
                useCase(
                    AddShoppingListItemsUseCase.Input(
                        byUserId = "user-1",
                        names = listOf("Milk", "Bread"),
                    ),
                )

            // then
            val expected =
                listOf(
                    shoppingListItem(
                        id = "id-1",
                        name = "Milk",
                        createdByUserId = "user-1",
                        createdAt = time1,
                        updatedAt = time1,
                    ),
                    shoppingListItem(
                        id = "id-2",
                        name = "Bread",
                        createdByUserId = "user-1",
                        createdAt = time2,
                        updatedAt = time2,
                    ),
                )

            assertEquals(expected, result.items)

            verifySuspend(exactly(2)) { repository.create(any()) }
        }
}
