plugins {
    alias(libs.plugins.paperweight.userdev)
}

dependencies {
    paperweight.paperDevBundle("1.21.10-R0.1-SNAPSHOT")
    compileOnly(project(":common"))
    compileOnly(libs.auth.lib.modern)
    compileOnly(libs.reflect)
}

tasks.reobfJar {
    paperweight {
        reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.REOBF_PRODUCTION
    }
}

tasks.assemble {
    dependsOn(tasks.reobfJar)
}