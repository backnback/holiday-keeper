plugins {
	java
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.planit"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
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
	// Base
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	runtimeOnly("com.mysql:mysql-connector-j")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// Validation
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// webfulx
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	// JUnit
	testImplementation("org.junit.jupiter:junit-jupiter")

	// Swagger UI
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

	// Spring Retry
	implementation("org.springframework.retry:spring-retry")


	// 임시 의존성 (Google Sheets API)
	implementation("com.google.api-client:google-api-client:2.0.0")
	implementation("com.google.apis:google-api-services-sheets:v4-rev20220927-2.0.0")
	implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")

	// 임시 의존성 (K-means 알고리즘)
	implementation("org.apache.commons:commons-math3:3.6.1")

}

tasks.withType<Test> {
	useJUnitPlatform()
	testLogging {
		events("passed", "skipped", "failed")
	}
}

tasks.compileJava {
	options.compilerArgs.add("-parameters")
}
