plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.apollographql.apollo") version "4.0.0"
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs")
    kotlin("kapt")
}
apollo {
    service("service") {
        packageName.set("com.imagetovideoapp")
        mapScalarToUpload("Upload")
        schemaFile.set(file("src/main/graphql/com/imagetovideoapp/schema.graphqls"))
    }
}



android {
    namespace = "com.imagetovideoapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.imagetovideoapp"
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
            buildConfigField("boolean", "USE_FAKE_REPO", "false")
        }
        debug {
            buildConfigField("boolean", "USE_FAKE_REPO", "false")
        }
        create("dummyTest") {
            initWith(getByName("debug"))
            manifestPlaceholders["hostName"] = "internal.example.com"
            applicationIdSuffix = ".debugStaging"
            buildConfigField("boolean", "USE_FAKE_REPO", "true")

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
        viewBinding = true
        buildConfig = true

    }
}

dependencies {

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.3")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation("io.reactivex.rxjava3:rxjava:3.1.6")
    implementation("com.squareup.retrofit2:adapter-rxjava3:2.9.0")
    implementation(libs.androidx.activity)

    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")


    // Unit test
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:3.11.2")
    testImplementation("androidx.test.ext:junit:1.2.1")
    testImplementation("androidx.test:core:1.6.1")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Kotlin coroutines and Flow for asynchronous data flow
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.8.3")
    implementation("androidx.fragment:fragment-ktx:1.8.1")

    // Retrofit for API requests
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp for logging and network interceptors
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")

    implementation("io.coil-kt:coil:2.4.0")


    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.apollographql.apollo:apollo-runtime")
    implementation("com.apollographql.apollo:apollo-api")

    implementation ("androidx.media3:media3-exoplayer:1.0.0-beta01")
    implementation ("androidx.media3:media3-ui:1.0.0-beta01")

}