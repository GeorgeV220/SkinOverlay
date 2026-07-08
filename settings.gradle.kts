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
include("bukkit:versions:mc1_17")
include("bukkit:versions:mc1_18")
include("bukkit:versions:mc1_18_2")
include("bukkit:versions:mc1_19")
include("bukkit:versions:mc1_19_3")
include("bukkit:versions:mc1_19_4")
include("bukkit:versions:mc1_20")
include("bukkit:versions:mc1_20_2")
include("bukkit:versions:mc1_20_3")
include("bukkit:versions:mc1_20_5")
include("bukkit:versions:mc1_21")
include("bukkit:versions:mc1_21_2")
include("bukkit:versions:mc1_21_4")
include("bukkit:versions:mc1_21_5")
include("bukkit:versions:mc1_21_6")
include("bukkit:versions:mc1_21_9")
include("velocity")