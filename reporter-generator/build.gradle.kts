plugins {
    kotlin("jvm")
    kotlin("kapt")
    `maven-publish`
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.auto.service:auto-service:1.0-rc4")
    implementation("org.springframework:spring-context:5.3.9")
    implementation("io.micrometer:micrometer-core:1.7.3")
    kapt("com.google.auto.service:auto-service:1.0-rc4")
    implementation(project(":reporter-api"))
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
            pom {
                name.set("reporter-generator")
                description.set("reporter-generator $version")
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