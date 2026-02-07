package org.tindalos.principle.infrastructure

import java.io.File

object BuildPathUtils {

  // Ordered list of directories to check (main sources first, then test sources)
  // Gradle separates Scala and Java classes
  // Maven mixes all classes together
  private val allDirectories = List(
    "./build/classes/scala/main/",  // Gradle Scala main
    "./build/classes/java/main/",   // Gradle Java main
    "./target/classes/",             // Maven main
    "./build/classes/scala/test/",  // Gradle Scala test
    "./build/classes/java/test/",   // Gradle Java test
    "./target/test-classes/"         // Maven test
  )

  /**
   * Returns the classes directory path, supporting both Maven and Gradle build systems.
   * Checks for Gradle's build directories (Scala and Java) first, then falls back to Maven's target directory.
   *
   * @return The path to the classes directory (with trailing slash)
   */
  private def getClassesDirectory: String = {
    // Try to find the first directory that exists
    allDirectories.find(dir => new File(dir).exists()).getOrElse("./target/classes/")
  }

  /**
   * Returns the classes directory path for a specific package, checking both main and test directories.
   * This is useful when test classes are in test directory but we need to analyze them.
   * Supports both Scala and Java classes in both Maven and Gradle build systems.
   *
   * @param packageName The package name to look for (e.g., "org.tindalos.principletest")
   * @return The path to the directory containing the package (with trailing slash)
   */
  def getClassesDirectoryForPackage(packageName: String): String = {
    val packagePath = packageName.replaceAll("\\.", "/")

    // Check if package exists in each directory and return the first match
    allDirectories
      .find(dir => new File(dir + packagePath).exists())
      .getOrElse(getClassesDirectory) // Fall back to default
  }

}

