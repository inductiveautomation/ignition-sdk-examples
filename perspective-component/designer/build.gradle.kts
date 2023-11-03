plugins {
    `java-library`
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}


dependencies {
    api(projects.common)
    compileOnly(libs.ignition.common)
    compileOnly(libs.google.jsr305)
    compileOnly(libs.ignition.designer.api)
    compileOnly(libs.ignition.perspective.common)
    compileOnly(libs.ignition.perspective.designer)
}


