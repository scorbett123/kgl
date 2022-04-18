import java.io.FileInputStream
import java.util.*

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("maven-publish")
}

//artifactId = "kgl-glfw"
group = "com.danielgergely.kgl"
version = rootProject.ext["currentVersion"] as String

repositories {
    mavenCentral()

    maven(url = "https://maven.danielgergely.com/repository/mirror")

    maven(url = "https://maven.pkg.github.com/Dominaezzz/kgl") {
        credentials {
            username = getLocalProperty("GITHUB_USER")
            password = getLocalProperty("GITHUB_TOKEN")
        }
    }
}

val kglVersion = "0.1.11"

kotlin {
    /* Targets configuration omitted. 
    *  To find out how to configure the targets, please follow the link:
    *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */

    linuxX64()
    mingwX64()
    if (org.gradle.internal.os.OperatingSystem.current().isMacOsX) {
        macosX64()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(project(":kgl"))

                implementation("com.kgl:kgl-core:$kglVersion")
                implementation("com.kgl:kgl-glfw:$kglVersion")
                implementation("com.kgl:kgl-glfw-static:$kglVersion") // For GLFW static binaries
                implementation("com.kgl:kgl-opengl:$kglVersion")
                implementation("com.kgl:kgl-vulkan:$kglVersion")
                implementation("com.kgl:kgl-glfw-vulkan:$kglVersion")
                implementation("com.kgl:kgl-stb:$kglVersion")
            }
        }
        val nativeMain by creating {
            dependsOn(commonMain)
        }
        val linuxX64Main by getting {
            dependsOn(nativeMain)
        }
        val mingwX64Main by getting {
            dependsOn(nativeMain)
        }
        val macosX64Main by getting {
            dependsOn(nativeMain)
        }
    }
}

fun getLocalProperty(key: String): String? =
    rootDir.resolve("local.properties")
        .let { file ->
            if (file.exists()) {
                FileInputStream(file)
            } else {
                null
            }
        }?.use { fileInputStream ->
            val prop = Properties()
            prop.load(fileInputStream)
            prop.getProperty(key)
        } ?: System.getenv(key)
