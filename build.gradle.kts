plugins {
  // Version via libs.versions.toml
  alias(libs.plugins.spotless) apply false
  alias(libs.plugins.spotbugs) apply false
}

allprojects {
  group = "dev.bloks"
  version = "0.1.0-SNAPSHOT"

  repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    // TerraformersMC (Mod Menu)
    maven("https://maven.terraformersmc.com/releases/")
  }
}

subprojects {
  // Ensure Java toolchain is consistent for Loom
  extensions.findByType(org.gradle.jvm.toolchain.JavaToolchainService::class.java)
  tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
  }

  // Lint + formatting + static analysis
  apply(plugin = "checkstyle")
  apply(plugin = "com.diffplug.spotless")
  apply(plugin = "com.github.spotbugs")

  configure<CheckstyleExtension> {
    toolVersion = "10.16.0"
    // Use the repo-root config for all modules instead of per-module paths
    configDirectory.set(rootProject.file("config/checkstyle"))
    isShowViolations = true
  }

  extensions.configure<com.diffplug.gradle.spotless.SpotlessExtension>("spotless") {
    java {
      target("**/*.java")
      googleJavaFormat()
      trimTrailingWhitespace()
      endWithNewline()
    }
  }

  tasks.withType<com.github.spotbugs.snom.SpotBugsTask>().configureEach {
    reports.create("html") {
      required.set(true)
      outputLocation.set(file("build/reports/spotbugs/${name}.html"))
    }
  }
}
