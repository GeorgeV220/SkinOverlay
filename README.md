# SkinOverlay
[![](https://img.shields.io/github/v/release/GeorgeV220/SkinOverlay?label=LATEST%20VERSION&style=for-the-badge)](https://github.com/GeorgeV220/SkinOverlay/releases/latest)
[![](https://img.shields.io/github/downloads/GeorgeV220/SkinOverlay/total?style=for-the-badge)](https://github.com/GeorgeV220/SkinOverlay/releases)

You can use the source code to do whatever you want but do not upload sell it or upload it without my permission (except
github)

# Adding SkinOverlay as a dependency to your build system

### Maven

You can have your project depend on SkinOverlay as a dependency through the following code snippets:

```xml

<project>
    <repositories>
        <repository>
            <id>reposilite-repository</id>
            <name>GeorgeV22 Repository</name>
            <url>https://repo.georgev22.com/releases</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.georgev22</groupId>
            <artifactId>skinoverlay</artifactId>
            <version>7.0.0</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

### Gradle

You can include SkinOverlay into your gradle project using the following lines:

```groovy
repositories {
    maven {
        url "https://repo.georgev22.com/releases"
    }
}

dependencies {
    compileOnly "com.georgev22:skinoverlay:7.0.0"
}
```

# Building SkinOverlay

### Gradle
SkinOverlay can be built by running the following: `gradle clean build :multiver:mc-1-17:reobfJar :multiver:mc-1-18:reobfJar :multiver:mc-1-18-2:reobfJar :multiver:mc-1-19:reobfJar :multiver:mc-1-19-3:reobfJar :multiver:mc-1-19-4:reobfJar :multiver:mc-1-20:reobfJar shadowJar`. The resultant jar is built and written
to `build/libs/skinoverlay-{version}.jar`.

The build directories can be cleaned instead using the `gradle clean` command.

If you want to clean (install) and build the plugin use `gradle clean build :multiver:mc-1-17:reobfJar :multiver:mc-1-18:reobfJar :multiver:mc-1-18-2:reobfJar :multiver:mc-1-19:reobfJar :multiver:mc-1-19-3:reobfJar :multiver:mc-1-19-4:reobfJar :multiver:mc-1-20:reobfJar shadowJar` command.

# Contributing

SkinOverlay is an open source `GNU General Public License v3.0` licensed project. I accept contributions through pull
requests, and will make sure to credit you for your awesome contribution.
