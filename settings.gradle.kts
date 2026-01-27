pluginManagement {
  repositories {
    // Prefer Fabric's Maven first to resolve Loom reliably
    maven("https://maven.fabricmc.net/")
    gradlePluginPortal()
    mavenCentral()
  }
  resolutionStrategy {
    eachPlugin {
      if (requested.id.id == "fabric-loom") {
        useModule("net.fabricmc:fabric-loom:${requested.version}")
      }
    }
  }
}

rootProject.name = "bloks"

include(":mods:beautiful_day_counter")
