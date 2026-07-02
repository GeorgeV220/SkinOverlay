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
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")

    implementation(project(":common")) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    quark(libs.bstats.velocity)
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

    compileOnly(libs.log4j.api)
}

configurations.configureEach {

}

quark {
    platform = "velocity"
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
}

tasks.shadowJar {
    archiveBaseName.set("skinoverlay")
    archiveClassifier.set("velocity")
}

tasks.named("publish") {
    dependsOn("shadowJar")
}