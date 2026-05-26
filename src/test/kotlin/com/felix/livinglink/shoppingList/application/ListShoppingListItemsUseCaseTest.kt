package com.felix.livinglink.shoppingList.application

import com.felix.livinglink.shoppingList.domain.ShoppingListItemQuery
import com.felix.livinglink.shoppingList.domain.ShoppingListItemRepository
import com.felix.livinglink.shoppingList.domain.ShoppingListItemSort
import com.felix.livinglink.shoppingList.domain.shoppingListItem
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ListShoppingListItemsUseCaseTest {
    private val repository = mock<ShoppingListItemRepository>()

    private val useCase =
        ListShoppingListItemsUseCase(
            shoppingListItemRepository = repository,
        )

    private val item1 = shoppingListItem(id = "id-1", name = "Milk")
    private val item2 = shoppingListItem(id = "id-2", name = "Bread")

    @Test
    fun `passes the query to the repository and returns its result`() =
        runTest {
            val query =
                ShoppingListItemQuery(
                    completed = false,
                    limit = 50,
                    sort = ShoppingListItemSort.NameAscending,
                )

            everySuspend { repository.find(query) } returns listOf(item1, item2)

            val result =
                useCase(
                    ListShoppingListItemsUseCase.Input(query = query),
                )

            assertEquals(
                ListShoppingListItemsUseCase.Output(items = listOf(item1, item2)),
                result,
            )

            verifySuspend(exactly(1)) { repository.find(query) }
        }
}
