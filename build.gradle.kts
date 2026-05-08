plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlint)
    application
}

group = "com.felix"
version = "1.0.0-SNAPSHOT"

application {
    mainClass = "com.felix.livinglink.LivingLinkMcpMainKt"
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
    testImplementation(libs.kotlin.test)
}
