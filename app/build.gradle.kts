
plugins {
//    id("com.android.application")
    id("com.android.application")
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.helloworld"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        ndk {
            abiFilters.add("armeabi-v7a")
            abiFilters.add("arm64-v8a")
        }

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

    // option to avoid compressing TF Lite model file
//    aaptOptions {
//        noCompress("tflite")
//        noCompress("lite")
//    }
 androidResources {
     noCompress += "tflite"
 }
    buildFeatures {
        mlModelBinding = true
    }
}

dependencies {
    implementation("com.google.android.gms:play-services-wearable:18.1.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-tasks:18.0.2")

    // Tensorflow Lite
    implementation("org.tensorflow:tensorflow-lite:+")
    implementation("org.tensorflow:tensorflow-lite:0.1.2-nightly")
    // This dependency adds the necessary TF op support.
    implementation("org.tensorflow:tensorflow-lite-select-tf-ops:0.0.0-nightly-SNAPSHOT")
    implementation("org.tensorflow:tensorflow-lite-select-tf-ops:2.5.0")

    // Tensorflow Lite dependencies
    implementation("org.tensorflow:tensorflow-lite-task-vision-play-services:0.4.2")
    implementation("com.google.android.gms:play-services-tflite-gpu:16.1.0")


    // tf lite gpu
    implementation("org.tensorflow:tensorflow-lite-gpu:+")
    // tf lite support library
    implementation("org.tensorflow:tensorflow-lite-support:+")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.1.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}