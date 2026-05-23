package com.felix.livinglink.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.api.verify.assertTrue
import kotlin.test.Test

class FeatureBoundariesTest {
    private val core = Layer("Core", "com.felix.livinglink.core..")
    private val shoppingList = Layer("ShoppingList", "com.felix.livinglink.shoppingList..")
    private val user = Layer("User", "com.felix.livinglink.user..")

    @Test
    fun `features depend on core, but core knows no feature`() {
        Konsist
            .scopeFromProduction()
            .assertArchitecture {
                core.dependsOnNothing()

                user.dependsOn(core)
                user.doesNotDependOn(shoppingList)

                shoppingList.dependsOn(core, user)
            }
    }

    @Test
    fun `shoppingList domain does not depend on user`() {
        Konsist
            .scopeFromProduction()
            .files
            .filter { it.packagee?.name?.startsWith("com.felix.livinglink.shoppingList.domain") == true }
            .assertTrue { file ->
                file.imports.none { it.name.startsWith("com.felix.livinglink.user.") }
            }
    }

    @Test
    fun `shoppingList application does not depend on user`() {
        Konsist
            .scopeFromProduction()
            .files
            .filter { it.packagee?.name?.startsWith("com.felix.livinglink.shoppingList.application") == true }
            .assertTrue { file ->
                file.imports.none { it.name.startsWith("com.felix.livinglink.user.") }
            }
    }
}
