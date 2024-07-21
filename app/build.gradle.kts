import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    signingConfigs {
        create("posta") {

            val keystorePropertiesFile = rootProject.file("keystore.properties")
            val keystoreProperties = Properties()
            keystoreProperties.load(FileInputStream(keystorePropertiesFile))

            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }
    namespace = "com.pluk.fiveballs"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pluk.fiveballs"
        minSdk = 24
        targetSdk = 34
        versionCode = 4
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val appName = "fiveballs"
        setProperty("archivesBaseName", "${appName}-v${versionName}")
        //setProperty("archivesBaseName", "${applicationId}-v${versionName}(${versionCode})")
    }

    buildTypes {

        getByName("debug") {

            // BuildType config
            isDebuggable = true
            applicationIdSuffix = ".debug"

            // Params
            val admobID = "ca-app-pub-3940256099942544/9214589741"

            manifestPlaceholders["admobID"] = "$admobID"
            manifestPlaceholders["appIcon"] = "@mipmap/ic_launcher_debug"

            buildConfigField("String", "SERVICE", "\"http://10.0.2.2:5002/fiveballs-bc2c7/us-central1/\"")
            buildConfigField("String", "ADMOB_APPID", "\"$admobID\"")
        }

        create("staging") {

            // BuildType config
            initWith(getByName("debug"))
            applicationIdSuffix = ".staging"

            // Shrinking and minification
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // Params
            val admobID = "ca-app-pub-3940256099942544/9214589741"

            manifestPlaceholders["admobID"] = "$admobID"
            manifestPlaceholders["appIcon"] = "@mipmap/ic_launcher_staging"

            buildConfigField("String", "SERVICE", "\"https://us-central1-fiveballs-bc2c7.cloudfunctions.net/\"")
            buildConfigField("String", "ADMOB_APPID", "\"$admobID\"")
        }

        getByName("release") {

            // Signing config
            signingConfig = signingConfigs.getByName("posta")

            // Shrinking and minification
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // Params
            val admobID = "ca-app-pub-6064071708465213~3296842365"

            manifestPlaceholders += mapOf()
            manifestPlaceholders["admobID"] = "$admobID"
            manifestPlaceholders["appIcon"] = "@mipmap/ic_launcher"

            buildConfigField("String", "SERVICE", "\"https://us-central1-fiveballs-bc2c7.cloudfunctions.net/\"")
            buildConfigField("String", "ADMOB_APPID", "\"$admobID\"")
        }
    }

    flavorDimensions += "tier"
    productFlavors {
        create("free") {
            dimension = "tier"
            applicationId = "com.pluk.fiveballs"
        }

        create("paid") {
            dimension = "tier"
            applicationId = "com.pluk.fiveballs"
            //applicationId = "com.pluk.fiveballs.noads"
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

//    applicationVariants.all {
//        val variant = this
//        val appName = "fiveballs"
//        variant.outputs.map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
//            .forEach { output ->
//                //val outputFileName = "fiveballs-${variant.buildType.name}-${variant.versionName}.apk"
//                val outputFileName = "$appName-${buildType.name}-$versionName.${if (variant.buildType.isMinifyEnabled) "aab" else "apk"}"
//                output.outputFileName = outputFileName
//            }
//    }

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

    implementation("com.squareup.retrofit2:retrofit:2.10.0")
    implementation("com.squareup.retrofit2:converter-gson:2.10.0")
    implementation("com.google.code.gson:gson:2.10")
    implementation("com.squareup.okhttp3:logging-interceptor:3.14.1")

    implementation("com.google.android.gms:play-services-ads:23.1.0")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))

    // Add the dependencies for the Crashlytics and Analytics libraries
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-config")

}
