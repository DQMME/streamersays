plugins {
    kotlin("jvm") version "1.5.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "de.dqmme"
version = "1.0"

repositories {
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    maven(url = "https://oss.sonatype.org/content/repositories/central")

    maven(url = "https://papermc.io/repo/repository/maven-public/")

    maven(url = "https://repo.codemc.io/repository/maven-snapshots/")

    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    compileOnly("org.spigotmc", "spigot-api", "1.16.5-R0.1-SNAPSHOT")

    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")

    implementation("net.axay:kspigot:1.16.29")
}

tasks {
    jar {
        enabled = false
    }
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        relocate("stdlib", "${project.group}.${project.name.toLowerCase()}.shadow.stdlib")
        relocate("com.squareup.okhttp3:okhttp:5.0.0-alpha.2", "${project.group}.${project.name.toLowerCase()}.shadow.com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
        relocate("net.axay:kspigot:1.16.29", "${project.group}.${project.name.toLowerCase()}.shadow.net.axay:kspigot:1.16.29")
    }
}