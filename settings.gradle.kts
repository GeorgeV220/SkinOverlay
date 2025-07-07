pluginManagement {
    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.maven.apache.org/maven2/")
        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
    }
}

rootProject.name = "skinoverlay"

include("build-info")
include("common")
include("bukkit")
include("bukkit:versions:mc1_17_R1")
include("bukkit:versions:mc1_18_R1")
include("bukkit:versions:mc1_18_R2")
include("bukkit:versions:mc1_19_R1")
include("bukkit:versions:mc1_19_R2")
include("bukkit:versions:mc1_19_R3")
include("bukkit:versions:mc1_20_R1")
include("bukkit:versions:mc1_20_R2")
include("bukkit:versions:mc1_20_R3")
include("bukkit:versions:mc1_20_R4")
include("bukkit:versions:mc1_21_R1")
include("bukkit:versions:mc1_21_R2")
include("bukkit:versions:mc1_21_R3")
include("bukkit:versions:mc1_21_R4")
include("bukkit:versions:mc1_21_R5")