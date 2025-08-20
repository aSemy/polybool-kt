plugins {
  kotlin("multiplatform") version "2.2.0"
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
    commonTest {
      dependencies {
        implementation("de.infix.testBalloon:testBalloon-framework-core:0.6.0-K2.2.0")
        implementation("de.infix.testBalloon:testBalloon-integration-kotest-assertions:0.6.0-K2.2.0")

        implementation("io.kotest:kotest-assertions-core:6.0.0")
      }
    }
  }
}
