// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript { 
    val navVersion = "2.3.5"
    val kotlinVersion = "1.6.10"
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
        classpath("com.google.gms:google-services:4.3.10")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        maven {
            setUrl("https://jitpack.io")
        }
        google()
        mavenCentral()
    }
    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).all {
        kotlinOptions {
            freeCompilerArgs =
                listOf(
                    *kotlinOptions.freeCompilerArgs.toTypedArray(),
                    "-Xuse-experimental=androidx.compose.foundation.ExperimentalAnimationApi",
                    "-Xuse-experimental=androidx.compose.foundation.ExperimentalFoundationApi",
                    "-Xuse-experimental=androidx.compose.ExperimentalComposeApi",
                    "-Xuse-experimental=androidx.compose.material.ExperimentalMaterialApi",
                    "-Xuse-experimental=androidx.compose.runtime.ExperimentalComposeApi",
                    "-Xuse-experimental=androidx.compose.ui.ExperimentalComposeUiApi"
                )
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}