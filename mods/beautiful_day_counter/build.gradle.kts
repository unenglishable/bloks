plugins {
  alias(libs.plugins.fabric.loom)
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(21))
  withSourcesJar()
}

dependencies {
  minecraft(libs.minecraft)
  mappings("net.fabricmc:yarn:${libs.versions.yarn.get()}:v2")
  modImplementation(libs.fabric.loader)
  modImplementation(libs.fabric.api)
  // Optional integration with Mod Menu for config UI
  modCompileOnly(libs.modmenu)
  modRuntimeOnly(libs.modmenu)
}

tasks.processResources {
  // Expand version into fabric.mod.json if needed
  inputs.property("version", project.version)
  filesMatching("fabric.mod.json") {
    expand(mapOf("version" to project.version))
  }
}

// Configure archives base name for nicer artifact name
base {
  archivesName.set("beautiful_day_counter")
}
