plugins {
    java
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.L5-G7"
version = "0.0.1-SNAPSHOT"
description = "MealCraft"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web") // Logback is included automatically here, we don't need any async logs, because app is small
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation ("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.h2database:h2")
    implementation("org.apache.httpcomponents.client5:httpclient5")
    implementation("org.apache.httpcomponents.core5:httpcore5")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.5.5")
    implementation("io.jsonwebtoken:jjwt:0.13.0")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation(project(":mealcraft-starter-external-recipes"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}
