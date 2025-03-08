plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose") // 🔹 Compose Compiler Plugin を追加
}
android {
    namespace = "com.example.myapplication"
    compileSdk = 34
    // 32に固定
    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 32  // 32に固定
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}
dependencies {
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.5.1")
    // 📌 API 32 で動作する Compose のバージョンを使用
    implementation("androidx.compose.material3:material3:1.0.0") // 1.1.x や beta は避ける
// Material3 の適切なバージョン
    implementation("androidx.compose.ui:ui:1.2.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.2.1")
    implementation("androidx.compose.foundation:foundation:1.2.1")
    implementation("androidx.compose.runtime:runtime:1.2.1")
    implementation("androidx.compose.runtime:runtime-saveable:1.2.1")
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.5.3")
    implementation(libs.play.services.location)
    implementation(libs.androidx.ui.android)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.2.1")
    debugImplementation("androidx.compose.ui:ui-tooling:1.2.1")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.2.1")


}