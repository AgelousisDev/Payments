// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript { 
    val navVersion = "2.5.0-alpha01"
    val kotlinVersion = "1.6.10"
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.2")
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
                    "-Xopt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                    "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi",
                    "-Xopt-in=androidx.compose.runtime.ExperimentalComposeApi",
                    "-Xopt-in=androidx.compose.ui.ExperimentalComposeUiApi",
                    "-Xopt-in=com.google.accompanist.pager.ExperimentalPagerApi"
                )
        }
    }
}
/**
 * -Xopt-in=androidx.compose.foundation.ExperimentalAnimationApi
 * -Xopt-in=androidx.compose.ExperimentalComposeApi
 */

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}