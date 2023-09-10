import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
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
    compileOnly("io.papermc.paper:paper-api:$paperVersion")
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