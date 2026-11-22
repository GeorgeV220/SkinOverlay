plugins {
    alias(libs.plugins.paperweight.userdev)
}

java.disableAutoTargetJvm()

dependencies {
    paperweight.paperDevBundle("26.1.2.build.+")
    compileOnly(project(":common"))
    compileOnly(libs.auth.lib.modern)
}

tasks.named("reobfJar") {
    enabled = false
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    withType<JavaCompile>().configureEach {
        options.release = 21
    }
}