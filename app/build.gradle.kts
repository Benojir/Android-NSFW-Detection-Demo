plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "zo.dimyon.nudedetectionai"
    compileSdk = 34

    defaultConfig {
        applicationId = "zo.dimyon.nudedetectionai"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Add the TensorFlow Lite dependency
    implementation (libs.tensorflow.lite)

// Optional: If using TensorFlow Lite GPU Delegate for performance
    implementation (libs.tensorflow.lite.gpu)

}