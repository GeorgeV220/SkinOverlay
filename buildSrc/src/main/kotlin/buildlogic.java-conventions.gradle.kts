plugins {
    `java-library`
    `maven-publish`
}

if (file("$rootDir/build.local.gradle.kts").exists()) {
    apply("$rootDir/build.local.gradle.kts")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://repo.georgev22.com/releases/")
    }
    maven {
        url = uri("https://repo.georgev22.com/snapshots/")
    }
    maven {
        url = uri("https://repo.codemc.org/repository/maven-public/")
    }
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://libraries.minecraft.net/")
        content {
            includeGroup("com.mojang")
        }
    }
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        url = uri("https://jcenter.bintray.com/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
    maven {
        url = uri("https://repo.inventivetalent.org/repository/public/")
    }
}