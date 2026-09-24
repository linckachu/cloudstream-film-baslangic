
import com.android.build.gradle.BaseExtension
import com.lagradost.cloudstream3.gradle.CloudstreamExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

buildscript {
    repositories {
        google()
        mavenCentral()

        maven("https://jitpack.io") {
            metadataSources {
                mavenPom()
                artifact()
                ignoreGradleMetadataRedirection()
            }
        }
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.7.3")

        classpath(
            "com.github.recloudstream:gradle:master-SNAPSHOT"
        )

        classpath(
            "org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0"
        )
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()

        maven("https://jitpack.io") {
            metadataSources {
                mavenPom()
                artifact()
                ignoreGradleMetadataRedirection()
            }
        }
    }
}

fun Project.cloudstream(
    configuration: CloudstreamExtension.() -> Unit
) = extensions
    .getByName<CloudstreamExtension>("cloudstream")
    .configuration()

fun Project.android(
    configuration: BaseExtension.() -> Unit
) = extensions
    .getByName<BaseExtension>("android")
    .configuration()

subprojects {

    apply(plugin = "com.android.library")
    apply(plugin = "kotlin-android")
    apply(plugin = "com.lagradost.cloudstream3.gradle")

    cloudstream {
        setRepo(
            System.getenv("GITHUB_REPOSITORY")
                ?: "linckachu/cloudstream-film-baslangic"
        )
    }

    android {
        namespace = "org.example.filmkaynak"

        compileSdkVersion(35)

        defaultConfig {
            minSdk = 21
            targetSdk = 35
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }

    tasks.withType<KotlinJvmCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)

            freeCompilerArgs.addAll(
                "-Xno-call-assertions",
                "-Xno-param-assertions",
                "-Xno-receiver-assertions"
            )
        }
    }

    dependencies {
        val implementation by configurations

        implementation(
            "com.github.recloudstream.cloudstream:library:-SNAPSHOT"
        )

        implementation(kotlin("stdlib"))

        implementation(
            "com.github.Blatzar:NiceHttp:0.4.11"
        )

        implementation(
            "org.jsoup:jsoup:1.18.3"
        )
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
