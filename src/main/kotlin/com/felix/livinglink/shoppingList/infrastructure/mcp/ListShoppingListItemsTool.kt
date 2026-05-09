package com.felix.livinglink.shoppingList.infrastructure.mcp

import com.felix.livinglink.infrastructure.mcp.McpToolRegistrar
import com.felix.livinglink.infrastructure.mcp.catchingToolErrors
import com.felix.livinglink.infrastructure.mcp.optionalBooleanArgument
import com.felix.livinglink.infrastructure.mcp.optionalIntArgument
import com.felix.livinglink.infrastructure.mcp.optionalStringArgument
import com.felix.livinglink.infrastructure.mcp.toolSuccess
import com.felix.livinglink.shoppingList.application.ListShoppingListItemsUseCase
import com.felix.livinglink.shoppingList.domain.ShoppingListItemQuery
import com.felix.livinglink.shoppingList.domain.ShoppingListItemSort
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import org.koin.core.annotation.Single

@Single(binds = [McpToolRegistrar::class])
class ListShoppingListItemsTool(
    private val listShoppingListItemsUseCase: ListShoppingListItemsUseCase,
) : McpToolRegistrar {
    override fun register(server: Server) {
        server.addTool(
            name = "list_shopping_list_items",
            description = "Lists shopping list items with optional filtering, sorting and limit.",
            inputSchema = shoppingListItemsQuerySchema(),
        ) { request ->
            catchingToolErrors {
                val arguments = request.params.arguments
                val limit =
                    requireNotNull(arguments.optionalIntArgument("limit")) {
                        "limit is required."
                    }

                require(limit in 1..MAX_LIMIT) {
                    "limit must be between 1 and $MAX_LIMIT."
                }

                val query =
                    ShoppingListItemQuery(
                        completed = arguments.optionalBooleanArgument("completed"),
                        limit = limit,
                        sort =
                            shoppingListItemSort(
                                sortBy = arguments.optionalStringArgument("sortBy"),
                                sortDirection = arguments.optionalStringArgument("sortDirection"),
                            ),
                    )

                val items = listShoppingListItemsUseCase(query)

                val text =
                    if (items.isEmpty()) {
                        "No shopping list items found."
                    } else {
                        items.joinToString(separator = "\n") { item ->
                            val status = if (item.completed) "done" else "open"

                            "- [$status] ${item.name} (id: ${item.id}, createdAt: ${item.createdAt}, updatedAt: ${item.updatedAt})"
                        }
                    }

                toolSuccess(text)
            }
        }
    }

    private fun shoppingListItemsQuerySchema(): ToolSchema =
        ToolSchema(
            properties =
                buildJsonObject {
                    putJsonObject("completed") {
                        put("type", "boolean")
                        put("description", "Optional completion status filter.")
                    }

                    putJsonObject("limit") {
                        put("type", "integer")
                        put("description", "Maximum number of items to return.")
                        put("minimum", 1)
                        put("maximum", MAX_LIMIT)
                    }

                    putJsonObject("sortBy") {
                        put("type", "string")
                        put("description", "Field to sort by.")
                        putJsonArray("enum") {
                            add("createdAt")
                            add("updatedAt")
                            add("name")
                        }
                    }

                    putJsonObject("sortDirection") {
                        put("type", "string")
                        put("description", "Sort direction.")
                        putJsonArray("enum") {
                            add("asc")
                            add("desc")
                        }
                    }
                },
            required = listOf("limit"),
        )

    private fun shoppingListItemSort(
        sortBy: String?,
        sortDirection: String?,
    ): ShoppingListItemSort {
        val normalizedSortBy = sortBy ?: "createdAt"
        val normalizedSortDirection = sortDirection ?: "desc"

        return when (normalizedSortBy to normalizedSortDirection) {
            "createdAt" to "asc" -> ShoppingListItemSort.CreatedAtAscending
            "createdAt" to "desc" -> ShoppingListItemSort.CreatedAtDescending
            "updatedAt" to "asc" -> ShoppingListItemSort.UpdatedAtAscending
            "updatedAt" to "desc" -> ShoppingListItemSort.UpdatedAtDescending
            "name" to "asc" -> ShoppingListItemSort.NameAscending
            "name" to "desc" -> ShoppingListItemSort.NameDescending
            else -> error("Unsupported sort: sortBy=$sortBy, sortDirection=$sortDirection.")
        }
    }

    private companion object {
        const val MAX_LIMIT = 1000
    }
}
