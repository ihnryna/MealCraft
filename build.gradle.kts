plugins {
    java
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.springdoc.openapi-gradle-plugin") version "1.9.0" //плагінн додає задачу generateOpenApiDocs
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
    implementation("org.springframework.boot:spring-boot-starter-web")
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
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// налаштування плагіна для генерації OpenAPI документації
openApi {
    apiDocsUrl.set("http://localhost:8080/v3/api-docs")
    outputDir.set(file("$projectDir/openapi"))
    outputFileName.set("mealcraft-openapi.json")
}

// Ми робимо таск 'build' залежним від 'generateOpenApiDocs'.
// Це гарантує, що документація буде згенерована перед тим, як проєкт спакується в .jar.
//Якщо генерація документації впаде, весь 'build' зупиниться.
// Це запобіжник, який не дає зламаній доці потрапити у збірку.
tasks.named("build") {
    dependsOn("generateOpenApiDocs")
}

