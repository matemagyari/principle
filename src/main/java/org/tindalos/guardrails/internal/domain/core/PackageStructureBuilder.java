package org.tindalos.guardrails.internal.domain.core;

import java.util.List;

/**
 * Builds a package tree rooted at the requested base package.
 */
public interface PackageStructureBuilder {

    Package build(List<Package> packages, String basePackage);
}
