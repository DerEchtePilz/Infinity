import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
	id("com.github.johnrengelman.shadow") version "8.1.1"
	id("xyz.jpenilla.run-paper") version "2.1.0"
	id("io.papermc.paperweight.userdev") version "1.5.5"
}

group = "io.github.derechtepilz"
version = "0.0.1"
description = "Infinity"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	gradlePluginPortal()
	maven {
		url = uri("https://repo.papermc.io/repository/maven-public/")
	}

	maven {
		url = uri("https://oss.sonatype.org/content/groups/public/")
	}

	maven {
		url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
	}

	maven {
		url = uri("https://repo.maven.apache.org/maven2/")
	}
	mavenLocal()
	mavenCentral()
}

val commandAPIVersion: String by project
val kotlinVersion: String by project
val paperVersion: String by project

dependencies {
	implementation(project(":infinity-api"))
	implementation("dev.jorel:commandapi-bukkit-shade:9.2.0")
	compileOnly("com.google.code.gson:gson:2.10.1")
	paperweight.paperDevBundle(paperVersion)
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
	options.encoding = "UTF-8"
}

tasks.withType<ProcessResources> {
	val properties = mapOf(
		"version" to project.version
	)

	filteringCharset = "UTF-8"

	inputs.properties(properties)

	filesMatching(listOf("paper-plugin.yml", "plugin.yml")) {
		expand(properties)
	}
}

tasks.withType<ShadowJar> {
	dependencies {
		include(dependency("io.github.derechtepilz:infinity-api:${project.version}"))
		include(dependency("dev.jorel:commandapi-bukkit-shade"))
	}
	minimize()
}