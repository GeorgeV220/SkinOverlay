import org.gradle.api.tasks.compile.JavaCompile

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("buildlogic.java-conventions")
    alias(libs.plugins.gradleup.shadow)
    id("org.bxteam.quark")
    id("net.fabricmc.fabric-loom") version "1.19.3"
}

apply(from = "$rootDir/gradle/publish.gradle")

base {
    archivesName.set(providers.gradleProperty("artifact"))
}


val shade by configurations.creating {
    isTransitive = false
}

val quarkShade by configurations.creating {
    isTransitive = true
}

val adventureInclude = configurations.create("adventureInclude") {
    isTransitive = false
}

loom {
    splitEnvironmentSourceSets()

    mods {
        create("skinoverlay") {
            sourceSet(sourceSets.main.get())
        }
    }
}

fabricApi {
    configureDataGeneration {
        client = false
    }
}

repositories {
    maven {
        url = uri("https://maven-central.storage-download.googleapis.com/maven2/")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")

    implementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")

    shade(implementation(project(":common"))!!)
    quarkShade(implementation("org.bxteam.quark:fabric:1.3.0")!!)

    implementation(libs.adventure.platform.fabric)
    adventureInclude(libs.adventure.platform.fabric)
    implementation(libs.adventure.fabric.serializer.legacy)
    adventureInclude(libs.adventure.fabric.serializer.legacy)

    quark(libs.hikari) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    quark(libs.gson)

    // MINESKIN CLIENT
    quark(libs.mineskinclient.client) {
        exclude(group = "com.google.code.gson", module = "gson")
        exclude(group = "com.google.guava", module = "guava")
    }
    quark(libs.mineskinclient.java11) {
        exclude(group = "com.google.code.gson", module = "gson")
        exclude(group = "com.google.guava", module = "guava")
    }

    // YAML
    quark(libs.yamlconfiguration) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    quark(libs.jedis)

    quark(libs.jspecify)
    quark(libs.mongodb.driver.sync)
}

quark {
    platform = "fabric"
    repositories {
        includeProjectRepositories();
    }

    relocate("org.mineskin", "${project.property("packageName")}.lib.mineskin")
    relocate("com.google.gson", "${project.property("packageName")}.lib.gson")
    relocate("com.google.errorprone", "${project.property("packageName")}.lib.gson.errorprone")
    relocate("com.zaxxer", "${project.property("packageName")}.lib.zaxxer")
    relocate("org.bstats", "${project.property("packageName")}.lib.bstats")
    relocate("org.bspfsystems.yamlconfiguration", "${project.property("packageName")}.lib.yaml")
    relocate("org.yaml.snakeyaml", "${project.property("packageName")}.lib.yaml")
    relocate("org.intellij.lang", "${project.property("packageName")}.lib.jetbrains")
    relocate("org.jetbrains", "${project.property("packageName")}.lib.jetbrains")
    relocate("org.json", "${project.property("packageName")}.lib.json")
    relocate("org.apache.commons.pool2", "${project.property("packageName")}.lib.pool2")
    relocate("redis.clients", "${project.property("packageName")}.lib.jedis")
    relocate("org.bson", "${project.property("packageName")}.lib.bson")
    relocate("com.mongodb", "${project.property("packageName")}.lib.mongodb")
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.parent?.property("minecraft_version"))
    inputs.property("loader_version", project.parent?.property("loader_version"))

    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "version" to project.version,
                "minecraft_version" to project.parent?.property("minecraft_version"),
                "loader_version" to project.parent?.property("loader_version"),
            )
        )
    }
}

val targetJavaVersion = 25

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    withSourcesJar()
}

tasks.named<ShadowJar>("shadowJar") {
    configurations = listOf(shade, quarkShade)

    archiveClassifier.set("fabric")
}

loom.nestJars(tasks.named<ShadowJar>("shadowJar"), adventureInclude)

tasks.named("publish") {
    dependsOn(tasks.named("shadowJar"))
}
