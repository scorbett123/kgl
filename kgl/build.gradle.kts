plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("maven-publish")
}

repositories {
    mavenCentral()
}

group = "com.danielgergely.kgl"
version = currentVersion

kotlin {
    jvm()

    js(BOTH) {
        browser {
            testTask {} // To run tests with browser.
        }
    }

    linuxX64()
    mingwX64()

    if (isMacOs()) {
        macosX64()
        iosArm64()
        iosX64()
    }
    explicitApi()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }

        val linuxX64Main by getting { }
        val mingwX64Main by getting { }

        val appleOnlySourceSets = if (isMacOs()) {
            val macosX64Main by getting { }
            val iosArm64Main by getting { }
            val iosX64Main by getting { }

            listOf(macosX64Main, iosArm64Main, iosX64Main)
        } else {
            listOf()
        }

        val nativeMain by sourceSets.creating {
            dependsOn(commonMain)

            linuxX64Main.dependsOn(this)
            mingwX64Main.dependsOn(this)

            appleOnlySourceSets.forEach { it.dependsOn(this) }
        }
    }
}

fun isMacOs() = org.gradle.internal.os.OperatingSystem.current().isMacOsX

publishing {
    addRepositoryIfPresent(project)
}
