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

    implementation(project(":common"))
    implementation(libs.bstats.velocity)
    implementation(libs.adventure.platform.api)
}

configurations.configureEach {

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