package org.tindalos.principle.infrastructure.service.jdepend;

import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import jdepend.framework.PackageFilter;
import org.tindalos.principle.infrastructure.BuildPathUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Runs JDepend analysis on compiled class directories, returning the analyzed packages.
 * Supports both Maven and Gradle build output layouts via {@link BuildPathUtils}.
 */
public class JDependRunner {

    public static List<JavaPackage> preparePackages(String rootPackage, boolean filterEnabled) {
        try {
            var jDepend = new JDepend();
            var directory = BuildPathUtils.getClassesDirectoryForPackage(rootPackage)
                    + rootPackage.replace('.', '/');
            jDepend.addDirectory(directory);

            if (filterEnabled) {
                var filter = PackageFilter.all();
                filter.accept(rootPackage);
                jDepend.setFilter(filter);
            }

            jDepend.addPackage(rootPackage);

            @SuppressWarnings("unchecked")
            var packages = (Collection<JavaPackage>) jDepend.analyze();
            return List.copyOf(packages);
        } catch (IOException e) {
            throw new RuntimeException("Failed to add directory for package: " + rootPackage, e);
        }
    }
}
