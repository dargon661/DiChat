plugins {
    id("com.android.application")

    id("com.google.gms.google-services")

}

android {
    namespace = "adar.dar.chatapp"
    compileSdk = 34

    packagingOptions{
        exclude("META-INF/DEPENDENCIES")
    }

    defaultConfig {
        applicationId = "adar.dar.chatapp"
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
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    implementation("com.hbb20:ccp:2.5.0")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.android.material:material:1.9.0")
    implementation ("com.github.bumptech.glide:glide:4.13.2")
    implementation ("com.google.firebase:firebase-auth:23.2.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.13.2")
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    implementation ("com.github.dhaval2404:imagepicker:2.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.google.auth:google-auth-library-oauth2-http:1.19.0")

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")
}


