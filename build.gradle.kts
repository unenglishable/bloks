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
    configDirectory.set(file("config/checkstyle"))
    isShowViolations = true
  }

  spotless {
    java {
      target("**/*.java")
      // Google Java Format
      googleJavaFormat()
      // Trim and end with newline
      trimTrailingWhitespace()
      endWithNewline()
    }
  }

  spotbugs {
    toolVersion.set("4.8.6")
    effort.set(com.github.spotbugs.snom.Effort.DEFAULT)
    reportLevel.set(com.github.spotbugs.snom.Confidence.DEFAULT)
  }

  tasks.withType<com.github.spotbugs.snom.SpotBugsTask>().configureEach {
    reports.create("html") {
      required.set(true)
      outputLocation.set(file("build/reports/spotbugs/${name}.html"))
    }
  }
}
