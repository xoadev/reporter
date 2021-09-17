import java.util.Properties

val autoservice_version: String by project
val micrometer_version: String by project
val kotlin_compile_testing_version: String by project

plugins {
    kotlin("jvm")
    kotlin("kapt")
    `maven-publish`
    signing
    id("org.jetbrains.dokka")
}

val dokkaHtml by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class)

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    dependsOn(dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputDirectory)
}

// Stub secrets to let the project sync and build without the publication values set up
ext["signing.keyId"] = null
ext["signing.password"] = null
ext["signing.secretKeyRingFile"] = null
ext["ossrhUsername"] = null
ext["ossrhPassword"] = null

// Grabbing secrets from local.properties file or from environment variables, which could be used on CI
val secretPropsFile = project.rootProject.file("local.properties")

if (secretPropsFile.exists()) {
    secretPropsFile.reader().use {
        Properties().apply {
            load(it)
        }
    }.onEach { (name, value) ->
        ext[name.toString()] = value
    }
} else {
    ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
    ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
    ext["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
    ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
    ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
}

fun getExtraString(name: String) = ext[name]?.toString()

dependencies {
    implementation(kotlin("stdlib"))
    compileOnly("com.google.auto.service:auto-service:$autoservice_version")
    implementation("io.micrometer:micrometer-core:$micrometer_version")
    kapt("com.google.auto.service:auto-service:$autoservice_version")
    implementation(project(":reporter-api"))
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:$kotlin_compile_testing_version")
    testImplementation(kotlin("test"))
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")
sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")

tasks.test {
    useJUnitPlatform()
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {

    repositories {
        maven {
            name = "sonatype"
            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = getExtraString("ossrhUsername")
                password = getExtraString("ossrhPassword")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            artifact(javadocJar)
            pom {
                name.set("reporter-generator")
                description.set("Library to build a reporter implementation that reports logs and metrics")
                url.set("https://github.com/xoadev/reporter")
                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        name.set("Javier Friere Riobó")
                        email.set("javier.freire@xoa.dev")
                        organization.set("xoa.dev")
                        organizationUrl.set("http://xoa.dev")
                    }
                }
                scm {
                    connection.set("git@github.com:xoadev/reporter.git")
                    url.set("https://github.com/xoadev/reporter/tree/main")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications)
}