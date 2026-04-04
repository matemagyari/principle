package org.tindalos.principle.infrastructure.core;

import java.io.File;
import java.util.List;

/**
 * Utility for resolving compiled classes directories across Maven and Gradle build systems.
 * Checks Gradle's separate Scala/Java output directories first, then falls back to Maven's target directory.
 */
public class BuildPathUtils {

    // Ordered list of directories to check (main sources first, then test sources).
    // Gradle separates Scala and Java classes; Maven mixes all classes together.
    private static final List<String> ALL_DIRECTORIES = List.of(
            "./build/classes/scala/main/",  // Gradle Scala main
            "./build/classes/java/main/",   // Gradle Java main
            "./target/classes/",             // Maven main
            "./build/classes/scala/test/",  // Gradle Scala test
            "./build/classes/java/test/",   // Gradle Java test
            "./target/test-classes/"         // Maven test
    );

    private static String getClassesDirectory() {
        return ALL_DIRECTORIES.stream()
                .filter(dir -> new File(dir).exists())
                .findFirst()
                .orElse("./target/classes/");
    }

    /**
     * Returns the classes directory containing the given package, checking both main and test
     * directories and supporting both Scala/Java classes in Maven and Gradle build systems.
     *
     * @param packageName the package name to look for (e.g. "org.tindalos.principletest")
     * @return the path to the directory containing the package (with trailing slash)
     */
    public static String getClassesDirectoryForPackage(String packageName) {
        var packagePath = packageName.replace('.', '/');
        return ALL_DIRECTORIES.stream()
                .filter(dir -> new File(dir + packagePath).exists())
                .findFirst()
                .orElseGet(BuildPathUtils::getClassesDirectory);
    }
}
