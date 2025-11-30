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

tasks.named("publish") {
    dependsOn("shadowJar")
}