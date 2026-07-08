plugins {
    id("buildlogic.java-conventions")
    alias(libs.plugins.gradleup.shadow)
    id("org.bxteam.quark") version "1.3.0"
}

apply(from = "$rootDir/gradle/publish.gradle")

repositories {
    maven {
        url = uri("https://maven-central.storage-download.googleapis.com/maven2/")
    }
}

dependencies {
    compileOnly(libs.folia.api)
    compileOnly(libs.placeholder.api)
    compileOnly(libs.auth.lib.legacy)

    quark(libs.bstats.bukkit)
    quark(libs.adventure.platform.bukkit)
    quark(libs.adventure.text.minimessage)
    quark(libs.adventure.text.serializer.legacy)

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

    implementation(project(":common"))
    implementation(project(":bukkit:versions:mc1_17", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_18", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_18_2", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_19", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_19_3", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_19_4", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_20", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_20_2", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_20_3", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_20_5", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_21", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_21_2", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_21_4", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_21_5", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_21_6", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_21_9", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_21_11", configuration = "reobf"))
}

quark {
    platform = "bukkit"
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
    relocate("net.kyori", "${project.property("packageName")}.lib.kyori")
    relocate("redis.clients", "${project.property("packageName")}.lib.jedis")
    relocate("org.bson", "${project.property("packageName")}.lib.bson")
    relocate("com.mongodb", "${project.property("packageName")}.lib.mongodb")
}

configurations.configureEach {
    resolutionStrategy {
        force(libs.adventure.platform.bukkit)
        force(libs.adventure.text.minimessage)
        force(libs.adventure.text.serializer.legacy)
        force(libs.folia.api)
        dependencySubstitution {
            substitute(module("org.spigotmc:spigot-api"))
                .using(module(libs.folia.api.get().toString()))
            substitute(module("org.bukkit:bukkit"))
                .using(module(libs.folia.api.get().toString()))
            substitute(module("io.papermc.paper:paper-api"))
                .using(module(libs.folia.api.get().toString()))
        }
    }
}

tasks.shadowJar {
    archiveBaseName.set("skinoverlay")
    archiveClassifier.set("bukkit")
}

tasks.named("publish") {
    dependsOn("shadowJar")
}