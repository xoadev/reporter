plugins {
    id("org.springframework.boot") version "2.5.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    java
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly(project(":reporter-api"))
    annotationProcessor(project(":reporter-generator"))
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}


