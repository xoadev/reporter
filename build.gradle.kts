plugins {
    kotlin("jvm") version "1.5.21" apply false
    id("org.jetbrains.dokka") version "1.5.0" apply false
}

allprojects {
    group = "dev.xoa.reporter"
    version = "1.0.1"

    repositories {
        mavenLocal()
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

subprojects {
    repositories {
        mavenCentral()
    }
}
