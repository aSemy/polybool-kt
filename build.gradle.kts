plugins {
  kotlin("multiplatform") version "2.2.10"
  kotlin("plugin.serialization") version "2.2.10"
  id("de.infix.testBalloon") version "0.6.0-K2.2.0"
}


kotlin {
  jvm()
  macosArm64()
  macosX64()
  linuxX64()
  linuxArm64()
  mingwX64()

  compilerOptions {
    freeCompilerArgs.addAll(
      listOf(
        "when-guards",
        "non-local-break-continue",
        "multi-dollar-interpolation",
        "annotation-target-all",
        "nested-type-aliases",
        "consistent-data-class-copy-visibility",
      ).map { "-X$it" }
    )
  }
  sourceSets {
    commonMain {
      dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
      }
    }
    commonTest {
      dependencies {
        implementation("de.infix.testBalloon:testBalloon-framework-core:0.6.0-K2.2.0")
        implementation("de.infix.testBalloon:testBalloon-integration-kotest-assertions:0.6.0-K2.2.0")

        implementation("io.kotest:kotest-assertions-core:6.0.0")

        implementation(kotlin("test"))
      }
    }
    jvmTest {
      dependencies {
        implementation("org.graalvm.polyglot:polyglot:24.2.2")
        implementation("org.graalvm.polyglot:js:24.2.2")

        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
      }
    }
  }
}
