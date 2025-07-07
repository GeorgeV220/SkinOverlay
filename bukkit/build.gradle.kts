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
    compileOnly(libs.adventure.api)
    compileOnly(libs.placeholder.api)
    compileOnly(libs.auth.lib.legacy)

    implementation(libs.bstats.bukkit)
    implementation(libs.adventure.platform.bukkit)

    implementation(project(":common"))
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
}

configurations.configureEach {
    resolutionStrategy {
        force(libs.adventure.platform.bukkit)
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
    archiveClassifier.set("shadow")
    relocate("org.mineskin", "${project.property("packageName")}.mineskin")
    relocate("com.google.gson", "${project.property("packageName")}.gson")
    relocate("com.google.errorprone", "${project.property("packageName")}.gson.errorprone")
    relocate("com.zaxxer", "${project.property("packageName")}.zaxxer")
    relocate("org.bstats", "${project.property("packageName")}.bstats")
    relocate("org.bspfsystems.yamlconfiguration", "${project.property("packageName")}.yaml")
    relocate("org.yaml.snakeyaml", "${project.property("packageName")}.yaml")
    relocate("org.intellij.lang", "${project.property("packageName")}.jetbrains")
    relocate("org.jetbrains", "${project.property("packageName")}.jetbrains")
    relocate("net.kyori", "${project.property("packageName")}.kyori")
}

tasks.named("publish") {
    dependsOn("shadowJar")
}