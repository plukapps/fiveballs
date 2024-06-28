plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.pluk.fiveballs"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pluk.fiveballs"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {

        getByName("debug") {
            isDebuggable = true

            applicationIdSuffix = ".debug"
            manifestPlaceholders["appIcon"] = "@drawable/app_icon_debug"

            buildConfigField("String", "SERVICE", "\"http://10.0.2.2:5002/fiveballs-bc2c7/us-central1/\"")
            buildConfigField("String", "ADMOB_APPID", "\"ca-app-pub-3940256099942544/9214589741\"")
        }

        create("staging") {
            initWith(getByName("debug"))
            applicationIdSuffix = ".staging"

            manifestPlaceholders["hostName"] = "internal.example.com"
            manifestPlaceholders["appIcon"] = "@drawable/app_icon_stage"

            buildConfigField("String", "SERVICE", "\"https://us-central1-fiveballs-bc2c7.cloudfunctions.net/\"")
            buildConfigField("String", "ADMOB_APPID", "\"ca-app-pub-3940256099942544/9214589741\"")
        }

        getByName("release") {
            isMinifyEnabled = false // TODO
            manifestPlaceholders += mapOf()
            manifestPlaceholders["appIcon"] = "@drawable/app_icon"

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")

            buildConfigField("String", "SERVICE", "\"https://us-central1-fiveballs-bc2c7.cloudfunctions.net/\"")
            buildConfigField("String", "ADMOB_APPID", "\"ca-app-pub-6064071708465213~3296842365\"")
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
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    lint {
        abortOnError = false
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.10")
    implementation("com.squareup.okhttp3:logging-interceptor:3.14.1")

    implementation("com.google.android.gms:play-services-ads:23.1.0")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))

    // Add the dependencies for the Crashlytics and Analytics libraries
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
}