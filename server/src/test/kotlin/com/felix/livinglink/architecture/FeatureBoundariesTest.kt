package com.felix.livinglink.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.api.verify.assertTrue
import kotlin.test.Test

class FeatureBoundariesTest {
    private val core = Layer("Core", "com.felix.livinglink.server.core..")
    private val user = Layer("User", "com.felix.livinglink.server.user..")
    private val shoppingList = Layer("ShoppingList", "com.felix.livinglink.server.shoppingList..")
    private val calendar = Layer("Calendar", "com.felix.livinglink.server.calendar..")
    private val session = Layer("Session", "com.felix.livinglink.server.session..")

    @Test
    fun `feature dependencies are correct`() {
        Konsist
            .scopeFromProduction()
            .assertArchitecture {
                core.dependsOnNothing()

                user.dependsOn(core)
                user.doesNotDependOn(shoppingList, calendar, session)

                shoppingList.dependsOn(core, user)
                shoppingList.doesNotDependOn(calendar, session)

                calendar.dependsOn(core, user)
                calendar.doesNotDependOn(shoppingList, session)

                session.dependsOn(core, user, shoppingList, calendar)
            }
    }

    @Test
    fun `session does not access delivery or infrastructure of other features`() {
        listOf("user", "shoppingList", "calendar").forEach { feature ->
            assertNoImportsFrom(
                featurePackagePrefix = "com.felix.livinglink.server.session",
                forbiddenImportPrefix = "com.felix.livinglink.server.$feature.delivery",
            )
            assertNoImportsFrom(
                featurePackagePrefix = "com.felix.livinglink.server.session",
                forbiddenImportPrefix = "com.felix.livinglink.server.$feature.infrastructure",
            )
        }
    }

    @Test
    fun `shoppingList and calendar may access user delivery mcp but not user infrastructure`() {
        listOf("shoppingList", "calendar").forEach { feature ->
            assertNoImportsFrom(
                featurePackagePrefix = "com.felix.livinglink.server.$feature",
                forbiddenImportPrefix = "com.felix.livinglink.user.server.infrastructure",
            )
        }
    }

    @Test
    fun `shoppingList domain and application do not depend on user`() {
        assertNoImportsFrom(
            featurePackagePrefix = "com.felix.livinglink.server.shoppingList.domain",
            forbiddenImportPrefix = "com.felix.livinglink.server.user.",
        )
        assertNoImportsFrom(
            featurePackagePrefix = "com.felix.livinglink.server.shoppingList.application",
            forbiddenImportPrefix = "com.felix.livinglink.server.user.",
        )
    }

    @Test
    fun `calendar domain and application do not depend on user`() {
        assertNoImportsFrom(
            featurePackagePrefix = "com.felix.livinglink.server.calendar.domain",
            forbiddenImportPrefix = "com.felix.livinglink.server.user.",
        )
        assertNoImportsFrom(
            featurePackagePrefix = "com.felix.livinglink.server.calendar.application",
            forbiddenImportPrefix = "com.felix.livinglink.server.user.",
        )
    }

    @Test
    fun `calendar and shoppingList do not depend on each other`() {
        assertNoImportsFrom(
            featurePackagePrefix = "com.felix.livinglink.server.calendar",
            forbiddenImportPrefix = "com.felix.livinglink.server.shoppingList.",
        )
        assertNoImportsFrom(
            featurePackagePrefix = "com.felix.livinglink.server.shoppingList",
            forbiddenImportPrefix = "com.felix.livinglink.server.calendar.",
        )
    }

    private fun assertNoImportsFrom(
        featurePackagePrefix: String,
        forbiddenImportPrefix: String,
    ) {
        Konsist
            .scopeFromProduction()
            .files
            .filter { it.packagee?.name?.startsWith(featurePackagePrefix) == true }
            .assertTrue { file ->
                file.imports.none { it.name.startsWith(forbiddenImportPrefix) }
            }
    }
}
