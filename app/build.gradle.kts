plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.recyclomate"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.recyclomate"
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.firebase.auth.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.firebase.bom)
    implementation ("com.google.android.gms:play-services-auth:20.5.0")

    // All:
    implementation (libs.cloudinary.android)

// Download + Preprocess:
    implementation (libs.cloudinary.android.download)
    implementation (libs.cloudinary.android.preprocess)

    implementation (libs.glide) // Check for the latest version
    kapt("com.github.bumptech.glide:compiler:4.15.1")
    implementation("com.google.firebase:firebase-firestore-ktx:24.1.2")

    //CircleImageVBiewer
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    val lifecycle_version = "2.8.6"
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

}