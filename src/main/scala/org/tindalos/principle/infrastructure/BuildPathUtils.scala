package org.tindalos.principle.infrastructure

import java.io.File

object BuildPathUtils {

  /**
   * Returns the classes directory path, supporting both Maven and Gradle build systems.
   * Checks for Gradle's build directory first (both main and test), then falls back to Maven's target directory.
   *
   * @return The path to the classes directory (with trailing slash)
   */
  def getClassesDirectory: String = {
    // Support both Maven and Gradle build directories
    // Try main directories first, then test directories
    val gradleMainDir = "./build/classes/scala/main/"
    val gradleTestDir = "./build/classes/scala/test/"
    val mavenMainDir = "./target/classes/"
    val mavenTestDir = "./target/test-classes/"

    if (new File(gradleMainDir).exists()) gradleMainDir
    else if (new File(gradleTestDir).exists()) gradleTestDir
    else if (new File(mavenMainDir).exists()) mavenMainDir
    else mavenTestDir
  }

  /**
   * Returns the classes directory path for a specific package, checking both main and test directories.
   * This is useful when test classes are in test directory but we need to analyze them.
   *
   * @param packageName The package name to look for (e.g., "org.tindalos.principletest")
   * @return The path to the directory containing the package (with trailing slash)
   */
  def getClassesDirectoryForPackage(packageName: String): String = {
    val packagePath = packageName.replaceAll("\\.", "/")

    // Check all possible locations
    val gradleMainDir = "./build/classes/scala/main/"
    val gradleTestDir = "./build/classes/scala/test/"
    val mavenMainDir = "./target/classes/"
    val mavenTestDir = "./target/test-classes/"

    // Check if package exists in each directory
    if (new File(gradleMainDir + packagePath).exists()) gradleMainDir
    else if (new File(gradleTestDir + packagePath).exists()) gradleTestDir
    else if (new File(mavenMainDir + packagePath).exists()) mavenMainDir
    else if (new File(mavenTestDir + packagePath).exists()) mavenTestDir
    else getClassesDirectory // Fall back to default
  }

}

