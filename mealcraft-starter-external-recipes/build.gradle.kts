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
	api("org.springframework.boot:spring-boot-autoconfigure:3.3.4")

	compileOnly("org.springframework.boot:spring-boot-configuration-processor:3.3.4")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.3.4")

	compileOnly("org.springframework:spring-web:6.1.12")
	compileOnly("com.fasterxml.jackson.core:jackson-databind:2.17.2")
	testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.5")
	testImplementation("org.junit.platform:junit-platform-launcher:1.13.4")

}
