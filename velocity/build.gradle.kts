plugins {
    id("buildlogic.java-conventions")
    alias(libs.plugins.gradleup.shadow)
}

apply(from = "$rootDir/gradle/publish.gradle")

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")

    implementation(project(":common")) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation(libs.bstats.velocity)
    compileOnly(libs.adventure.platform.api)
    compileOnly(libs.log4j.api)
}

configurations.configureEach {

}

tasks.shadowJar {
    archiveBaseName.set("skinoverlay")
    archiveClassifier.set("velocity")
    relocate("org.mineskin", "${project.property("packageName")}.mineskin")
    relocate("com.google.gson", "${project.property("packageName")}.gson")
    relocate("com.google.errorprone", "${project.property("packageName")}.gson.errorprone")
    relocate("com.zaxxer", "${project.property("packageName")}.zaxxer")
    relocate("org.bstats", "${project.property("packageName")}.bstats")
    relocate("org.bspfsystems.yamlconfiguration", "${project.property("packageName")}.yaml")
    relocate("org.yaml.snakeyaml", "${project.property("packageName")}.yaml")
    relocate("org.intellij.lang", "${project.property("packageName")}.jetbrains")
    relocate("org.jetbrains", "${project.property("packageName")}.jetbrains")
    relocate("org.json", "${project.property("packageName")}.json")
    relocate("org.apache.commons.pool2", "${project.property("packageName")}.pool2")
    relocate("redis.clients", "${project.property("packageName")}.jedis")
}

tasks.named("publish") {
    dependsOn("shadowJar")
}