plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.loginsignup"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.loginsignup"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true  // Enable DataBinding here
    }

    buildToolsVersion = "34.0.0"
}

dependencies {
    // Core Android libraries
    implementation("com.github.bumptech.glide:glide:4.14.2")
    implementation ("com.google.firebase:firebase-firestore:24.7.1")
    implementation ("com.google.firebase:firebase-analytics:21.5.0")
    implementation ("com.squareup.picasso:picasso:2.8")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation ("com.android.volley:volley:1.2.0")

    // Glide for image loading (Optional if you want to use Glide for image handling)
    implementation ("com.github.bumptech.glide:glide:4.15.0")

    // OkHttp for HTTP requests
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")

    // Gson for JSON parsing
    implementation ("com.google.code.gson:gson:2.8.8")


    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("androidx.recyclerview:recyclerview:1.3.1")

    // Cloudinary SDK
    implementation("com.cloudinary:cloudinary-android:3.0.2")

    // DataBinding runtime
    implementation("androidx.databinding:databinding-runtime:4.2.0") // Replace with your desired version

    // Firebase Authentication
    implementation(platform("com.google.firebase:firebase-bom:32.1.1"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.inappmessaging.ktx)

    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
