plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.koin.compiler)
    application
}

group = "com.felix"
version = "1.0.0-SNAPSHOT"

application {
    mainClass.set("com.felix.livinglink.LivingLinkMcpMainKt")
}

kotlin {
    jvmToolchain(21)
}

ktlint {
    version.set("1.6.0")
}

dependencies {
    implementation(libs.mcp.kotlin.sdk.server)
    implementation(libs.logback.classic)
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
    implementation(libs.mongodb.driver.kotlin.coroutine)
    implementation(libs.ktor.server.cio)

    testImplementation(libs.kotlin.test)
}
