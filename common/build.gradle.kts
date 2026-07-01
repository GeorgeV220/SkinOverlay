plugins {
    id("buildlogic.java-conventions")
    alias(libs.plugins.gradleup.shadow)
    alias(libs.plugins.blossom)
}

apply(from = "$rootDir/gradle/publish.gradle")

repositories {
    mavenCentral()
}

group = project.property("group") as String
description = project.property("description") as String
version = project.property("version") as String

dependencies {
    api(project(":build-info"))
    compileOnly(libs.skinsrestorer.api)
    // ADVENTURE
    compileOnly(libs.adventure.api)
    compileOnly(libs.adventure.platform.api)
    compileOnly(libs.adventure.text.minimessage)
    compileOnly(libs.adventure.text.serializer.legacy)

    // log4j
    compileOnly(libs.log4j.api)


    implementation(libs.hikari) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    implementation(libs.gson)

    // MINESKIN CLIENT
    implementation(libs.mineskinclient.client) {
        exclude(group = "com.google.code.gson", module = "gson")
        exclude(group = "com.google.guava", module = "guava")
    }
    implementation(libs.mineskinclient.java11) {
        exclude(group = "com.google.code.gson", module = "gson")
        exclude(group = "com.google.guava", module = "guava")
    }

    // YAML
    implementation(libs.yamlconfiguration) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    implementation(libs.jedis)

    implementation(libs.jspecify)
    implementation(libs.mongodb.driver.sync)
}

configurations.configureEach {
    resolutionStrategy {
        force(libs.adventure.text.serializer.legacy)
        force(libs.adventure.text.minimessage)
        force(libs.jetbrains.annotations)
    }
}


tasks.processResources {
    filesMatching("**.yml") {
        val props = mapOf(
            "pluginName" to project.property("pluginName"),
            "bungeeMain" to project.property("bungeeMain"),
            "bukkitMain" to project.property("bukkitMain"),
            "version" to version,
            "author" to project.property("author"),
            "packageName" to project.property("packageName"),
        )
        expand(props)
        filteringCharset = "UTF-8"
    }
}

tasks.shadowJar {
    archiveBaseName.set("skinoverlay")
    archiveClassifier.set("common")
}
