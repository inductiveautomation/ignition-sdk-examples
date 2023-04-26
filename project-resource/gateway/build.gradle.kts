plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.common)
    implementation(libs.ignition.gateway)
}

java {
    toolchain {
        languageVersion.set(libs.versions.java.map(JavaLanguageVersion::of))
    }
}
