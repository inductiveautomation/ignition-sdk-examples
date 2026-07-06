
plugins {
    `java-library`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    // declare our dependencies on ignition sdk elements.  These are defined in the gradle/libs.versions.toml file of
    // the root project for this module
    compileOnly(libs.ignition.gateway.api)
}
