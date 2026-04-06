fun versionCodeFromTag(): Int {
    val tag = System.getenv("GITHUB_REF_NAME") ?: return 1
    val parts = tag.removePrefix("v").split(".")
    if (parts.size != 3) return 1
    val major = parts[0].toIntOrNull() ?: return 1
    val minor = parts[1].toIntOrNull() ?: return 1
    val patch = parts[2].toIntOrNull() ?: return 1
    return major * 10000 + minor * 100 + patch
}

fun versionNameFromTag(): String {
    val tag = System.getenv("GITHUB_REF_NAME") ?: return "dev"
    return tag.removePrefix("v").ifEmpty { "dev" }
}

plugins {
  alias(libs.plugins.android.application)
}

android {
  namespace = "info.yuryv.hellocustomdiscover"
  compileSdk {
    version = release(36)
  }

  defaultConfig {
    applicationId = "info.yuryv.hellocustomdiscover"
    minSdk = 30
    targetSdk = 36
    versionCode = versionCodeFromTag()
    versionName = versionNameFromTag()

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("ANDROID_KEYSTORE_PATH") ?: ""
      if (keystorePath.isNotEmpty()) {
        storeFile = file(keystorePath)
        storePassword = System.getenv("ANDROID_STORE_PASSWORD") ?: ""
        keyAlias = System.getenv("ANDROID_KEY_ALIAS") ?: ""
        keyPassword = System.getenv("ANDROID_KEY_PASSWORD") ?: ""
      }
    }
  }

  buildTypes {
    release {
      signingConfig = signingConfigs.getByName("release")
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  buildFeatures {
    aidl = true
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
}

androidComponents {
  beforeVariants { variantBuilder ->
    if (variantBuilder.buildType == "release") {
      variantBuilder.enableUnitTest = true
    }
  }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
}