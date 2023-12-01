
plugins {
    `java-library`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}


dependencies {
    // compileOnly is the gradle equivalent to "provided" scope.  Here we resolve the dependencies via the
    // declarations in the gradle/libs.versions.toml file
    compileOnly(libs.ignition.common)
    compileOnly(libs.ignition.perspective.common)
    compileOnly(libs.google.guava)
    compileOnly(libs.ia.gson)
}
