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
  val props = mapOf(
    "version" to project.version,
    "fabric_loader" to libs.versions.fabricLoader.get(),
    "fabric_api" to libs.versions.fabricApi.get(),
    "minecraft" to libs.versions.minecraft.get()
  )
  inputs.properties(props)
  filesMatching("fabric.mod.json") {
    expand(props)
  }
}

// Configure archives base name for nicer artifact name
base {
  archivesName.set("beautiful_day_counter")
}
