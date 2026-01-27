import net.fabricmc.loom.task.RemapJarTask
import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
  alias(libs.plugins.fabric.loom)
}

loom {
  // Ensure src/client/java is compiled and on the classpath for runClient
  splitEnvironmentSourceSets()
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
  // SpotBugs annotations for targeted suppressions
  compileOnly(libs.spotbugsAnnotations)
  testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
}

val vc = extensions.getByType<VersionCatalogsExtension>().named("libs")

tasks.processResources {
  val props = mapOf(
    "version" to project.version.toString(),
    "fabric_loader" to vc.findVersion("fabric_loader").get().requiredVersion,
    "fabric_api" to vc.findVersion("fabric_api").get().requiredVersion,
    "minecraft" to vc.findVersion("minecraft").get().requiredVersion
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

tasks.named<RemapJarTask>("remapJar") {
  archiveClassifier.set("")
}

tasks.test {
  useJUnitPlatform()
}
