plugins {
    id("buildlogic.java-conventions")
    alias(libs.plugins.gradleup.shadow)
}

apply(from = "$rootDir/gradle/publish.gradle")

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.folia.api)
    compileOnly(libs.placeholder.api)
    compileOnly(libs.auth.lib.legacy)

    implementation(libs.bstats.bukkit)
    implementation(libs.adventure.platform.bukkit)
    implementation(libs.adventure.text.minimessage)
    implementation(libs.adventure.text.serializer.legacy)
    implementation(libs.reflect)

    implementation(project(":common")) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation(project(":bukkit:versions:mc1_17_R1", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_18_R1", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_18_R2", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_19_R1", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_19_R2", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_19_R3", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_20_R1", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_20_R2", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_20_R3", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_20_R4", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_21_R1", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_21_R2", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_21_R3", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_21_R4", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_21_R5", configuration = "reobf"))
    implementation(project(":bukkit:versions:mc1_21_R6", configuration = "reobf"))
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
    relocate("net.lenni0451.reflect", "${project.property("packageName")}.lib.reflect")
}

tasks.named("publish") {
    dependsOn("shadowJar")
}