plugins {
    alias(libs.plugins.paperweight.userdev)
}

dependencies {
    paperweight.paperDevBundle("1.21.5-R0.1-SNAPSHOT")
    compileOnly(project(":common"))
    compileOnly(libs.auth.lib.modern)
}

tasks.reobfJar {
    paperweight {
        reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.REOBF_PRODUCTION
    }
}

tasks.assemble {
    dependsOn(tasks.reobfJar)
}