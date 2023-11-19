plugins {
    id("java")
}

group = "io.github.derechtepilz"
version = "0.0.1"
description = "infinity-api"

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
	mavenLocal()
	mavenCentral()
}

dependencies {
	compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}