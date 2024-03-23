plugins {
  kotlin("multiplatform") version "1.9.23"
}

repositories {
  mavenCentral()
}

kotlin {
  sourceSets {
    nativeMain {
      dependencies {
        implementation("com.github.ajalt.clikt:clikt:4.2.2")
//        implementation("com.kgit2:kommand:2.0.1")
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0-RC.2")
      }
    }
  }

  macosX64("native") {
    binaries {
      executable()
    }
  }
}

tasks.withType<Wrapper> {
  gradleVersion = "8.1.1"
  distributionType = Wrapper.DistributionType.BIN
}