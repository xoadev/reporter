plugins {
    kotlin("jvm") version "1.5.30" apply false
    id("org.jetbrains.dokka") version "1.5.0" apply false
}

allprojects {
    group = "dev.xoa.reporter"
    version = "1.0.2"

    repositories {
        mavenLocal()
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}

subprojects {
    repositories {
        mavenCentral()
    }
}
