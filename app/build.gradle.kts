plugins {
    id("com.github.ben-manes.versions") version "0.42.0"
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
}

val kotlinVersion = "1.6.0"
val navVersion = "2.5.1"
val materialVersion = "1.7.0-alpha02"
val ktxCoreVersion = "1.9.0"
val activityVersion = "1.6.0-beta01"
val fragmentVersion = "1.5.0-beta01"
val firebaseBomVersion = "30.5.0"
val liveDataViewModelVersion = "2.5.0-beta01"
val composeVersion = "1.3.0-beta03"
val kotlinCoroutinesVersion = "1.6.2"
val appCompatVersion = "1.6.0-beta01"
val constraintLayoutVersion = "2.1.3"
val constraintLayoutComposeVersion = "1.1.0-alpha04"
val composeActivityVersion = "1.6.0"
val composeMaterialYouVersion = "1.0.0-beta03"
val lottieComposeVersion = "5.2.0"

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.agelousis.payments"
        minSdk = 26
        targetSdk = 33
        versionCode = 63
        versionName = "5.7"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    bundle {
        storeArchive {
            enable = false
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName(name = "debug")
            isDebuggable = true
            buildConfigField(type = "String", name = "VALID_PRODUCT_DATE", value = "\"\"")
            buildConfigField(type = "String", name = "FIREBASE_CLOUD_MESSAGING_URL", value = "\"https://fcm.googleapis.com/fcm/\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                    getDefaultProguardFile(
                            name = "proguard-android-optimize.txt"
                    ),
                    "proguard-rules.pro"
            )
            buildConfigField(type = "String", name = "VALID_PRODUCT_DATE", value = "\"\"")
            buildConfigField(type = "String", name = "FIREBASE_CLOUD_MESSAGING_URL", value = "\"https://fcm.googleapis.com/fcm/\"")
        }
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.1"
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    configurations {
        create("developImplementation")
    }
}
// 2020_11_24_10_00_00

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("com.google.android.material:material:$materialVersion")
    implementation("androidx.activity:activity-ktx:$activityVersion")
    implementation("androidx.fragment:fragment-ktx:$fragmentVersion")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.activity:activity-compose:$composeActivityVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.material3:material3:$composeMaterialYouVersion")
    implementation("androidx.compose.ui:ui-viewbinding:$composeVersion")
    implementation("androidx.compose.runtime:runtime-livedata:$composeVersion")
    implementation("androidx.constraintlayout:constraintlayout-compose:$constraintLayoutComposeVersion")
    implementation("com.google.accompanist:accompanist-pager:0.26.4-beta")
    implementation("io.coil-kt:coil-compose:2.2.2")
    implementation("androidx.viewpager2:viewpager2:1.1.0-beta01")
    implementation("androidx.appcompat:appcompat:$appCompatVersion")
    implementation("androidx.constraintlayout:constraintlayout:$constraintLayoutVersion")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.biometric:biometric-ktx:1.2.0-alpha05")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinCoroutinesVersion")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$liveDataViewModelVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$liveDataViewModelVersion")
    // ViewModel utilities for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$liveDataViewModelVersion")
    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$liveDataViewModelVersion")
    implementation(platform("com.google.firebase:firebase-bom:$firebaseBomVersion"))
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("androidx.core:core-ktx:$ktxCoreVersion")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("com.airbnb.android:lottie:5.2.0")
    implementation("com.airbnb.android:lottie-compose:$lottieComposeVersion")
    implementation("com.github.PhilJay:MPAndroidChart:3.1.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.8")
    implementation("me.dm7.barcodescanner:zxing:1.9.8")
    // Zxing
    implementation("com.google.zxing:core:3.5.0")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation(files("libs/itextpdf-5.5.13.jar"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.4-alpha07")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0-alpha07")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
}