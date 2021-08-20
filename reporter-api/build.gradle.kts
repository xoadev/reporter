plugins {
    java
    `maven-publish`
}

group = "dev.xoa.reporter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "reporter-api"

            from(components["java"])
        }
    }
}