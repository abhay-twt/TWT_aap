plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.photoapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.photoapp"
        minSdk = 26
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
        implementation(libs.firebase.firestore)
        implementation(libs.google.googleid)
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)

        implementation("com.google.android.gms:play-services-vision:20.1.3")

        // Import the Firebase BoM
        implementation(platform("com.google.firebase:firebase-bom:33.5.1"))

        // When using the BoM, you don't specify versions in Firebase library dependencies

        // Add the dependency for the Firebase SDK for Google Analytics
        implementation("com.google.firebase:firebase-analytics")

        // TODO: Add the dependencies for any other Firebase products you want to use
        // See https://firebase.google.com/docs/android/setup#available-libraries
        // For example, add the dependencies for Firebase Authentication and Cloud Firestore
        implementation("com.google.firebase:firebase-auth")
        implementation("com.google.firebase:firebase-firestore")
    }