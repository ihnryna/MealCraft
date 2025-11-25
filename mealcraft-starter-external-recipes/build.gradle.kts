val springBootVersion = "3.5.5"
val springBootAutoconfigureVersion = "3.3.4"
val springWebVersion = "6.1.12"
val junitLauncherVersion = "1.13.4"
val jacksonVersion = "2.17.2"

plugins {
	`java-library`
	`maven-publish`
}

group = "org.l5g7.mealcraft"
version = "0.0.1-SNAPSHOT"
description = "mealcraft-starter-external-recipes"

java {
	toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

repositories { mavenCentral() }

dependencies {
	api("org.springframework.boot:spring-boot-autoconfigure:$springBootAutoconfigureVersion")

	compileOnly("org.springframework.boot:spring-boot-configuration-processor:$springBootAutoconfigureVersion")
	compileOnly("org.springframework:spring-web:$springWebVersion")
	compileOnly("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:$springBootAutoconfigureVersion")

	testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
	testImplementation("org.junit.platform:junit-platform-launcher:$junitLauncherVersion")

}
