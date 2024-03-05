plugins {
    id("com.android.application")
}


android {

    namespace = "com.example.blankapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.blankapplication"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        buildFeatures {
            buildConfig = true
        }
        debug {
            // Add a field for the HTTP address in debug (dev) build
            buildConfigField("String", "DEV_BASE_URL", "\"http://192.168.1.141:8080/\"")
        }
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
    buildFeatures {
        viewBinding = true
    }

}

dependencies {
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")
    implementation ("com.github.bumptech.glide:glide:4.13.2")
    implementation("com.squareup.retrofit2:retrofit:2.4.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("org.projectlombok:lombok:1.18.30")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("org.mongodb:mongodb-driver-sync:4.2.3")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-paging:$room_version")

    // optional - RxJava2 support for Room
    implementation("androidx.room:room-rxjava2:$room_version")

    // optional - RxJava3 support for Room
    implementation("androidx.room:room-rxjava3:$room_version")
    implementation ("io.reactivex.rxjava3:rxjava:3.1.2")
    implementation ("io.reactivex.rxjava3:rxandroid:3.0.0")


}

