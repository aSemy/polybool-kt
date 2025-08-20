rootProject.name = "polybool-kt"

pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

  repositories {
    mavenCentral()

    // Declare the Node.js & Yarn download repositories
    // https://youtrack.jetbrains.com/issue/KT-55620/
    exclusiveContent {
      forRepository {
        ivy("https://nodejs.org/dist/") {
          name = "Node Distributions at $url"
          patternLayout { artifact("v[revision]/[artifact](-v[revision]-[classifier]).[ext]") }
          metadataSources { artifact() }
          content { includeModule("org.nodejs", "node") }
        }
      }
      filter { includeGroup("org.nodejs") }
    }

    exclusiveContent {
      forRepository {
        ivy("https://github.com/yarnpkg/yarn/releases/download") {
          name = "Yarn Distributions at $url"
          patternLayout { artifact("v[revision]/[artifact](-v[revision]).[ext]") }
          metadataSources { artifact() }
          content { includeModule("com.yarnpkg", "yarn") }
        }
      }
      filter { includeGroup("com.yarnpkg") }
    }

    ivy("https://github.com/") {
      name = "GitHub Release"
      // used to download YAML Test Suite data from GitHub
      patternLayout {
        artifact("[organization]/[module]/archive/[revision].[ext]")
        artifact("[organization]/[module]/archive/refs/tags/[revision].[ext]")
        artifact("[organization]/[module]/archive/refs/tags/v[revision].[ext]")
      }
      metadataSources { artifact() }
    }

    exclusiveContent {
      forRepository {
        ivy("https://github.com/WebAssembly/binaryen/releases/download") {
          name = "Binaryen Distributions at $url"
          patternLayout { artifact("version_[revision]/[module]-version_[revision]-[classifier].[ext]") }
          metadataSources { artifact() }
          content { includeModule("com.github.webassembly", "binaryen") }
        }
      }
      filter { includeGroup("com.github.webassembly") }
    }
  }
}
