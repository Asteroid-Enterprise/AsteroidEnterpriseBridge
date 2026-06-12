plugins {
    id("java")
    alias(libs.plugins.shadow)
}

allprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()

        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

        maven("https://repo.tcoded.com/releases") // folialib
    }

    dependencies {
        compileOnly(rootProject.libs.lombok)
        annotationProcessor(rootProject.libs.lombok)

        implementation(rootProject.libs.folialib)
    }

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(8))
        }
    }
}

dependencies {
    compileOnly(libs.spigot)
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}

tasks.shadowJar {
    archiveClassifier.set("")

    relocate("com.tcoded.folialib", "me.serbob.asteroidenterprisebridge.libs.folialib")
}