package org.tindalos.guardrails.internal.app;

import org.tindalos.guardrails.internal.domain.core.Package;

import java.util.List;

public interface PackageListBuilder {

    List<Package> build();
}
