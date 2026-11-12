plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.ancientkeys.keyboard"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.ancientkeys.keyboard"
        minSdk = 26
        targetSdk = 37
        versionCode = 2
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val requestedTasks = gradle.startParameter.taskNames
    val isReleaseTest = requestedTasks.any { it.contains("ReleaseUnitTest", ignoreCase = true) || it.contains("ReleaseAndroidTest", ignoreCase = true) }
    testBuildType = project.findProperty("testBuildType")?.toString() ?: if (isReleaseTest) "release" else "debug"

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
    lint {
        abortOnError = true
        checkReleaseBuilds = false
        disable.add("ObsoleteLintCustomCheck")
        disable.add("GradleDependency")
        disable.add("NewerVersionAvailable")
        disable.add("ObsoleteSdkInt")
        disable.add("UnusedResources")
        disable.add("UseKtx")
        disable.add("ModifierParameter")
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
    debugImplementation(libs.androidx.ui.tooling)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
}
