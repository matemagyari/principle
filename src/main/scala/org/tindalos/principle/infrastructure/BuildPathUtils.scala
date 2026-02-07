package org.tindalos.principle.infrastructure

import java.io.File

object BuildPathUtils {

  /**
   * Returns the classes directory path, supporting both Maven and Gradle build systems.
   * Checks for Gradle's build directory first, then falls back to Maven's target directory.
   *
   * @return The path to the classes directory (with trailing slash)
   */
  def getClassesDirectory: String = {
    // Support both Maven and Gradle build directories
    val gradleDir = "./build/classes/scala/main/"
    val mavenDir = "./target/classes/"

    if (new File(gradleDir).exists()) gradleDir
    else mavenDir
  }

}

