plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
//    id("kotlin-kapt")
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
    implementation(libs.firebase.storage.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(platform(libs.firebase.bom))
    implementation (libs.play.services.auth)

    // All:
    implementation (libs.cloudinary.android)

// Download + Preprocess:
    implementation (libs.cloudinary.android.download)
    implementation (libs.cloudinary.android.preprocess)

    implementation (libs.glide) // Check for the latest version
//    kapt("com.github.bumptech.glide:compiler:4.15.1")

    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore.ktx)
    implementation("com.google.firebase:firebase-storage:20.0.1")

    //CircleImageVBiewer
    implementation (libs.circleimageview)

    val lifecycle_version = "2.8.6"
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx.v240)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx)

    implementation (libs.mpandroidchart)
    implementation (libs.glide.v4160)
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0") // Gson converter
    implementation ("com.squareup.okhttp3:okhttp:4.9.1") // or the latest version
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation ("com.airbnb.android:lottie:6.0.0")
    implementation ("com.airbnb.android:lottie:6.0.0")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

}