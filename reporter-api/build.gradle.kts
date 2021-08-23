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
            pom {
                name.set("reporter-api")
                description.set("reporter-api $version")
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
