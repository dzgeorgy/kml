plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.ksp.api)
    implementation(libs.poet)
    implementation(project(":lib"))
}
