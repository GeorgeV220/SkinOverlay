import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("buildlogic.java-conventions")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.10"
    id("net.kyori.indra.git") version "3.1.3"
    alias(libs.plugins.blossom)
}

sourceSets {
    main {
        blossom {
            javaSources {
                property("pluginName", project.property("pluginName").toString())
                property("version", version.toString())
                property("description", rootProject.description)
                property("author", project.property("author").toString())
                property("url", "https://github.com/GeorgeV220/SkinOverlay")
                property("commit", indraGit.commit()?.name ?: "unknown")
                property("branch", indraGit.branch()?.name ?: "unknown")
                property("build_time", SimpleDateFormat("dd MMMM yyyy HH:mm:ss").format(Date()))
                property("ci_name", getRunnerName())
                property("ci_build_number", getBuildNumber())
            }
        }
    }
}

fun getRunnerName(): String {
    val githubActions = System.getenv("GITHUB_ACTIONS")
    if (githubActions != null && githubActions == "true") {
        return "github-actions"
    }

    return "local"
}

fun getBuildNumber(): String {
    return System.getenv("BUILD_NUMBER") ?: System.getenv("GITHUB_RUN_NUMBER") ?: "local"
}