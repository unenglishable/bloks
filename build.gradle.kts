plugins {
  // Version via libs.versions.toml plugin alias in subprojects
}

allprojects {
  group = "dev.bloks"
  version = "0.1.0-SNAPSHOT"

  repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
  }
}

subprojects {
  // Ensure Java toolchain is consistent for Loom
  extensions.findByType(org.gradle.jvm.toolchain.JavaToolchainService::class.java)
  tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
  }
}

