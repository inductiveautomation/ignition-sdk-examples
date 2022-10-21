
plugins {
    `java-library`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

dependencies {
    implementation(projects.common)
    // declare our dependencies on ignition sdk elements.  These are defined in the gradle/libs.versions.toml file of
    // the root project for this module
    compileOnly(libs.ignition.common)
    compileOnly(libs.ignition.gateway.api)
    compileOnly(libs.ignition.perspective.gateway)
    compileOnly(libs.ignition.perspective.common)
    compileOnly(libs.ia.gson)
}
