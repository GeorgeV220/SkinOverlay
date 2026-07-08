plugins {
    alias(libs.plugins.paperweight.userdev)
}

dependencies {
    paperweight.paperDevBundle("1.17.1-R0.1-SNAPSHOT")
    compileOnly(project(":common"))
}

tasks.reobfJar {
    paperweight {
        reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.REOBF_PRODUCTION
    }
}

tasks.assemble {
    dependsOn(tasks.reobfJar)
}