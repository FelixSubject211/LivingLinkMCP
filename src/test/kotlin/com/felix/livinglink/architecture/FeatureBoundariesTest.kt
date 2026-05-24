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

    @Test
    fun `features depend on core, but core knows no feature`() {
        Konsist
            .scopeFromProduction()
            .assertArchitecture {
                core.dependsOnNothing()

                user.dependsOn(core)
                user.doesNotDependOn(shoppingList, calendar)

                shoppingList.dependsOn(core, user)
                shoppingList.doesNotDependOn(calendar)

                calendar.dependsOn(core, user)
                calendar.doesNotDependOn(shoppingList)
            }
    }

    @Test
    fun `shoppingList domain does not depend on user`() {
        assertNoImportsFrom(
            featurePackagePrefix = "com.felix.livinglink.shoppingList.domain",
            forbiddenImportPrefix = "com.felix.livinglink.user.",
        )
    }

    @Test
    fun `shoppingList application does not depend on user`() {
        assertNoImportsFrom(
            featurePackagePrefix = "com.felix.livinglink.shoppingList.application",
            forbiddenImportPrefix = "com.felix.livinglink.user.",
        )
    }

    @Test
    fun `calendar domain does not depend on user`() {
        assertNoImportsFrom(
            featurePackagePrefix = "com.felix.livinglink.calendar.domain",
            forbiddenImportPrefix = "com.felix.livinglink.user.",
        )
    }

    @Test
    fun `calendar application does not depend on user`() {
        assertNoImportsFrom(
            featurePackagePrefix = "com.felix.livinglink.calendar.application",
            forbiddenImportPrefix = "com.felix.livinglink.user.",
        )
    }

    @Test
    fun `calendar does not depend on shoppingList`() {
        assertNoImportsFrom(
            featurePackagePrefix = "com.felix.livinglink.calendar",
            forbiddenImportPrefix = "com.felix.livinglink.shoppingList.",
        )
    }

    @Test
    fun `shoppingList does not depend on calendar`() {
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
