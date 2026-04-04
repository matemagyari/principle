package org.tindalos.principle.infrastructure.service.jdepend.classdependencies;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.tindalos.principle.domain.core.Node;
import org.tindalos.principle.infrastructure.BuildPathUtils;

/**
 * Creates dependency graph nodes by parsing compiled class files with JDepend support.
 */
public final class MyJDependRunner {

    private MyJDependRunner() {
    }

    public static String className(String fullName) {
        int innerClassIndex = fullName.indexOf('$');
        return innerClassIndex >= 0 ? fullName.substring(0, innerClassIndex) : fullName;
    }

    public static Set<Node> createNodesOfClasses(String rootPackage) {
        return createNodesOfClasses(rootPackage, null);
    }

    public static Set<Node> createNodesOfClasses(String rootPackage, String targetDir) {
        String classesDir = targetDir != null
                ? targetDir
                : BuildPathUtils.getClassesDirectoryForPackage(rootPackage);

        File rootDir = new File(classesDir + rootPackage.replace('.', '/'));
        File[] files = recursiveListFiles(rootDir);

        Map<String, Set<String>> dependenciesByClass = new HashMap<>();
        for (File file : files) {
            if (!file.getName().endsWith(".class")) {
                continue;
            }

            Clazz parsedClass = toClazz(file, rootPackage);
            dependenciesByClass.computeIfAbsent(className(parsedClass.name()), ignored -> new HashSet<>())
                    .addAll(parsedClass.dependencies());
        }

        Set<Clazz> classes = dependenciesByClass.entrySet().stream()
                .map(entry -> new Clazz(entry.getKey(), Set.copyOf(entry.getValue())))
                .collect(Collectors.toUnmodifiableSet());

        return clazzesToNodes(classes);
    }

    private static File[] recursiveListFiles(File file) {
        File[] directChildren = file.listFiles();
        if (directChildren == null) {
            return new File[0];
        }

        return Arrays.stream(directChildren)
                .flatMap(child -> child.isDirectory()
                        ? Arrays.stream(concat(new File[]{child}, recursiveListFiles(child)))
                        : Arrays.stream(new File[]{child}))
                .toArray(File[]::new);
    }

    private static File[] concat(File[] first, File[] second) {
        File[] combined = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, combined, first.length, second.length);
        return combined;
    }

    private static Clazz toClazz(File file, String rootPackage) {
        try {
            var javaClass = new MyClassFileParser(rootPackage).parse(file);
            Set<String> dependencies = javaClass.getDependencies().stream()
                    .map(Objects::toString)
                    .filter(dependency -> !dependency.equals(javaClass.getName()))
                    .collect(Collectors.toUnmodifiableSet());
            return new Clazz(javaClass.getName(), dependencies);
        } catch (IOException exception) {
            throw new UncheckedIOException("Failed to parse class file: " + file, exception);
        }
    }

    private static Set<Node> clazzesToNodes(Set<Clazz> classes) {
        return classes.stream()
                .map(clazz -> {
                    Set<String> dependantNames = classes.stream()
                            .filter(other -> other.dependencies().contains(clazz.name()))
                            .map(Clazz::name)
                            .collect(Collectors.toUnmodifiableSet());
                    return new Node(clazz.name(), clazz.dependencies(), dependantNames);
                })
                .collect(Collectors.toUnmodifiableSet());
    }

    private record Clazz(String name, Set<String> dependencies) {
    }
}
