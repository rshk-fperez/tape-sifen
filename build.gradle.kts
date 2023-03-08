import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.0"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	kotlin("plugin.jpa") version "1.6.21"
}

group = "com.roshka.tape"
version = "1.0.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
	maven{
		url = uri("[https://jaspersoft.jfrog.io/jaspersoft/third-party-ce-artifacts/")
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.roshka.sifen:rshk-jsifenlib:0.2.1")
	implementation("org.firebirdsql.jdbc:jaybird:4.0.6.java11")
	implementation("javax.xml.soap:javax.xml.soap-api:1.4.0")
	implementation("org.glassfish.metro:webservices-rt:2.4.4")
	implementation("org.glassfish.metro:webservices-api:2.4.4")
	implementation("net.sf.jasperreports:jasperreports:6.16.0")
	implementation("com.lowagie:itext:2.1.7")
	implementation("org.json:json:20190722")
	implementation("xmlpull:xmlpull:1.1.3.4a")
	implementation("xalan:xalan:2.7.0")
	implementation("com.google.zxing:core:3.3.0")
	implementation("com.google.zxing:javase:3.3.0")
	implementation("net.sf.barcode4j:barcode4j:2.1")
	implementation("org.apache.xmlgraphics:batik-bridge:1.11")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
