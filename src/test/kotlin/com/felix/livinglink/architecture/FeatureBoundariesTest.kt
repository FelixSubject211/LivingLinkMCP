package com.felix.livinglink.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.api.verify.assertTrue
import kotlin.test.Test

class FeatureBoundariesTest {
    private val core = Layer("Core", "com.felix.livinglink.core..")
    private val user = Layer("User", "com.felix.livinglink.user..")
    private val shoppingList = Layer("ShoppingList", "com.felix.livinglink.shoppingList..")
    private val calendar = Layer("Calendar", "com.felix.livinglink.calendar..")
    private val session = Layer("Session", "com.felix.livinglink.session..")

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
                featurePackagePrefix = "com.felix.livinglink.session",
                forbiddenImportPrefix = "com.felix.livinglink.$feature.delivery",
            )
            assertNoImportsFrom(
                featurePackagePrefix = "com.felix.livinglink.session",
                forbiddenImportPrefix = "com.felix.livinglink.$feature.infrastructure",
            )
        }
    }

    @Test
    fun `shoppingList and calendar may access user delivery mcp but not user infrastructure`() {
        listOf("shoppingList", "calendar").forEach { feature ->
            assertNoImportsFrom(
                featurePackagePrefix = "com.felix.livinglink.$feature",
                forbiddenImportPrefix = "com.felix.livinglink.user.infrastructure",
            )
        }
    }

    @Test
    fun `shoppingList domain and application do not depend on user`() {
        assertNoImportsFrom(
            featurePackagePrefix = "com.felix.livinglink.shoppingList.domain",
            forbiddenImportPrefix = "com.felix.livinglink.user.",
        )
        assertNoImportsFrom(
            featurePackagePrefix = "com.felix.livinglink.shoppingList.application",
            forbiddenImportPrefix = "com.felix.livinglink.user.",
        )
    }

    @Test
    fun `calendar domain and application do not depend on user`() {
        assertNoImportsFrom(
            featurePackagePrefix = "com.felix.livinglink.calendar.domain",
            forbiddenImportPrefix = "com.felix.livinglink.user.",
        )
        assertNoImportsFrom(
            featurePackagePrefix = "com.felix.livinglink.calendar.application",
            forbiddenImportPrefix = "com.felix.livinglink.user.",
        )
    }

    @Test
    fun `calendar and shoppingList do not depend on each other`() {
        assertNoImportsFrom(
            featurePackagePrefix = "com.felix.livinglink.calendar",
            forbiddenImportPrefix = "com.felix.livinglink.shoppingList.",
        )
        assertNoImportsFrom(
            featurePackagePrefix = "com.felix.livinglink.shoppingList",
            forbiddenImportPrefix = "com.felix.livinglink.calendar.",
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
