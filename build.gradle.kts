import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
	`jvm-test-suite`
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
	id("xyz.jpenilla.run-paper") version "2.1.0"
	id("io.papermc.paperweight.userdev") version "1.5.5"
}

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
    implementation("dev.jorel:commandapi-bukkit-shade:$commandAPIVersion")
    compileOnly("dev.jorel:commandapi-bukkit-kotlin:$commandAPIVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    compileOnly("com.google.code.gson:gson:2.10.1")
	testImplementation("org.mockito:mockito-core:4.6.1")
	testImplementation("com.github.seeseemelk:MockBukkit-v1.20:3.19.2")
	testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	paperweight.paperDevBundle(paperVersion)

	configurations.testRuntimeClasspath {
		exclude(group = "io.papermc.paper", module = "paper-server")
	}
}

group = "io.github.derechtepilz"
version = "0.0.1"
description = "Infinity"
java.sourceCompatibility = JavaVersion.VERSION_17

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

testing {
	suites {
		val test by getting(JvmTestSuite::class) {
			useJUnitJupiter()
		}
		register<JvmTestSuite>("integrationTest") {
			dependencies {
				implementation(project())
			}
			targets {
				all {
					testTask.configure {
						shouldRunAfter(test)
					}
				}
			}
		}
	}
}

tasks.withType<Test> {
	testLogging {
		exceptionFormat = TestExceptionFormat.FULL
		events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.STANDARD_ERROR, TestLogEvent.STANDARD_OUT, TestLogEvent.SKIPPED)
		showStandardStreams = true
	}
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

tasks.withType<KotlinCompile> {
    compilerOptions.languageVersion.set(KotlinVersion.KOTLIN_1_9)
    compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
}

tasks.withType<ShadowJar> {
    dependencies {
        include(dependency("dev.jorel:commandapi-bukkit-shade:$commandAPIVersion"))
        include(dependency("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"))
    }
    relocate("dev.jorel.commandapi", "io.github.derechtepilz.commandapi")
    minimize()
}