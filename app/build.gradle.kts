plugins {
    //alias(libs.plugins.android.application)
    //alias(libs.plugins.kotlin.android)
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.room")
    //id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.attendo"
    compileSdk = 34

    defaultConfig {

        applicationId = "com.example.attendo"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // Configuración de Room
    room {
        schemaDirectory("$projectDir/schemas")
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            //excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/{DEPENDENCIES,LICENSE,LICENSE.txt,NOTICE,NOTICE.txt}"
        }
    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.constraintlayout.compose.android)
    //implementation(libs.play.services.mlkit.barcode.scanning)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //otras
    implementation ("org.apache.poi:poi:5.2.3") // Versión estable, revisa la última versión disponible
    implementation ("org.apache.poi:poi-ooxml:5.2.3")// Para trabajar con formatos XLSX
    implementation("com.google.android.gms:play-services-base:18.0.1")
    //Material3
    implementation ("androidx.compose.material3:material3:1.1.0")
    implementation ("androidx.compose.material:material-icons-extended:1.0.1")


    implementation("androidx.compose.ui:ui:1.5.0")
    implementation ("androidx.compose.material:material:1.5.0")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.5.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    //Scaner
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    //Itext para el reporte
    implementation ("com.itextpdf:itext7-core:7.2.3")

    //Dependencias Romm
    implementation("androidx.room:room-runtime:2.5.2")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.5.2")
    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    // ZXing for QR Scanning
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    //dependencia para el reporte iText7
    implementation("com.itextpdf:itext7-core:7.1.14")

    //implementation("com.itextpdf:itext7-core:7.2.3")
    //navegacion
    implementation ("androidx.navigation:navigation-compose:2.6.0")

    // mas
    implementation("org.apache.pdfbox:pdfbox:3.0.0")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.itextpdf:itext7-core:7.2.2")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    //Enviar correo de recuperacion
    implementation ("com.sun.mail:android-mail:1.6.0")
    implementation ("com.sun.mail:android-activation:1.6.0")
    //Firebase para recuperacion de contrase;a
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-analytics")

}