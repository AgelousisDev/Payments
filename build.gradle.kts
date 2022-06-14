// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript { 
    val navVersion = "2.5.0-alpha01"
    val kotlinVersion = "1.6.21"
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.2.1")
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
                    "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                    "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
                    "-opt-in=androidx.compose.runtime.ExperimentalComposeApi",
                    "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
                    "-opt-in=com.google.accompanist.pager.ExperimentalPagerApi",
                    "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
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