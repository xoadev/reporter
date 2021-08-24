val ktor_version: String by project
val logback_version: String by project
val prometeus_version : String by project

plugins {
    application
    kotlin("jvm")
    kotlin("kapt")
}

application {
    mainClass.set("dev.xoa.reporter.ApplicationKt")
}

dependencies {
    implementation("io.ktor:ktor-metrics-micrometer:$ktor_version")
    implementation("io.micrometer:micrometer-registry-prometheus:$prometeus_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    compileOnly(project(":reporter-api"))
    kapt(project(":reporter-generator"))
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation(kotlin("test"))
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")
sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")

tasks.test {
    useJUnitPlatform()
}