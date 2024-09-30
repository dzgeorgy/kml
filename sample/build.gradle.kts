plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
}

application {
    mainClass = "dev.dzgeorgy.kml.ExamplesKt"
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":lib"))
    ksp(project(":compiler"))
}
