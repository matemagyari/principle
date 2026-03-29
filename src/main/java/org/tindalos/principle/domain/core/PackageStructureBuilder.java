package org.tindalos.principle.domain.core;

/**
 * Builds a package tree rooted at the requested base package.
 */
public interface PackageStructureBuilder {

    Package build(scala.collection.immutable.List<Package> packages, String basePackage);
}
