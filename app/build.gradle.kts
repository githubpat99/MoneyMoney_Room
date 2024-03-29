plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "1.8.21" // must be the correct version
    id("com.google.devtools.ksp") version "1.8.21-1.0.11"
    id("org.jetbrains.dokka") version "1.8.20"
}

android {
    namespace = "com.nickpatrick.swissmoneysaver"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.nickpatrick.swissmoneysaver"
        minSdk = 26
        targetSdk = 34
        versionCode = 4
        versionName = "1.0.2."

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        versionNameSuffix = "0002"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"

        }
    }

}

dependencies {
    // Import the Compose BOM
    implementation(platform("androidx.compose:compose-bom:2023.06.01"))
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.material3:material3:1.1.1")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.navigation:navigation-compose:2.7.1")

    //Room
    implementation("androidx.room:room-runtime:${rootProject.extra["room_version"]}")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    testImplementation("junit:junit:4.12")
    testImplementation("junit:junit:4.12")
    testImplementation("junit:junit:4.12")
    ksp("androidx.room:room-compiler:${rootProject.extra["room_version"]}")
    implementation("androidx.room:room-ktx:${rootProject.extra["room_version"]}")

    //Json
    implementation("com.google.code.gson:gson:2.10.1")


    //Csv Parser
    implementation("io.github.rybalkinsd:kohttp:0.12.0") // khttp for HTTP requests
    implementation("com.github.doyaaaaaken:kotlin-csv:1.9.2")

    //Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.android.gms:play-services-drive:17.0.0")

    // Guava
    implementation("com.google.guava:guava:24.1-jre")
// Guava fix
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")

//Drive
    implementation("com.google.api-client:google-api-client-android:1.23.0") {
        exclude(group = "org.apache.httpcomponents", module = "guava-jdk5")
    }
    implementation("com.google.apis:google-api-services-drive:v3-rev136-1.25.0") {
        exclude(group = "org.apache.httpcomponents", module = "guava-jdk5")
    }
// Ktor HTTP Client
    implementation("io.ktor:ktor-client-android:2.3.4")

// Ktor Client Core
    implementation("io.ktor:ktor-client-core:2.3.4")

// Square’s meticulous HTTP client for Java and Kotlin
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")

    // kotlinx-coroutines-android
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // runtime - Live Data
    implementation("androidx.compose.runtime:runtime-livedata:1.5.4") // Use the appropriate version

    // Video
    val compose_version = "1.5.0"
    implementation("androidx.compose.ui:ui:$compose_version")
    implementation("androidx.compose.material:material:$compose_version")
    implementation("androidx.compose.ui:ui-tooling:$compose_version")
    implementation("androidx.compose.foundation:foundation:$compose_version")
    implementation("androidx.compose.foundation:foundation-layout:$compose_version")
    implementation("androidx.compose.runtime:runtime-livedata:$compose_version")
    implementation("androidx.compose.runtime:runtime:$compose_version")
    implementation("androidx.compose.material:material-icons-extended:$compose_version")
    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07")
    implementation("com.google.android.exoplayer:exoplayer-core:2.19.1")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.19.1")



    // Testing
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:core-ktx:1.5.0")

    testImplementation("io.mockk:mockk:1.12.0") // Check for the latest version on MockK's website
    androidTestImplementation("io.mockk:mockk-android:1.12.0") // For Android instrumentation tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.1")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.1")
}