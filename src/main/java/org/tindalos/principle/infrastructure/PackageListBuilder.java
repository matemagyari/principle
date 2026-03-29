package org.tindalos.principle.infrastructure;

import org.tindalos.principle.domain.core.Package;

import java.util.List;

public interface PackageListBuilder {

    List<Package> build();
}
