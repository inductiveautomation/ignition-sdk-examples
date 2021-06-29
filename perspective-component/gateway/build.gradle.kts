
plugins {
    `java-library`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

dependencies {
    //we have a compile dependency on the common project.  Use the typesafe syntax here, but could also be written
    // as 'api(project(":common"))'. See https://docs.gradle.org/7.0/release-notes.html on Type-safe project accessors
    api(projects.common)

    // declare our dependencies on ignition sdk elements.  These are defined in the gradle/libs.versions.toml file of
    // this repo
    compileOnly(libs.ignition.common)
    compileOnly (libs.ignition.gateway.api)
    compileOnly (libs.ignition.perspective.gateway)
    compileOnly(libs.ia.gson)
}
