plugins {
    kotlin("jvm")
    kotlin("kapt")
    `maven-publish`
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.auto.service:auto-service:1.0-rc4")
    implementation("org.springframework:spring-context:5.3.9")
    implementation("io.micrometer:micrometer-registry-atlas:1.7.3")
    kapt("com.google.auto.service:auto-service:1.0-rc4")
    implementation(project(":reporter-api"))
    implementation(project(":reporter-shared"))
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.2")
    testImplementation(kotlin("test"))
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")
sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "reporter-spring"

            from(components["java"])
        }
    }
}