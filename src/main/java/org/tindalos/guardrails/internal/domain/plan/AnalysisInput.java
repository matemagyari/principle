package org.tindalos.guardrails.internal.domain.plan;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.tindalos.guardrails.internal.domain.constraints.PackageCouplingConstraints;
import org.tindalos.guardrails.internal.domain.constraints.ThirdParty;
import org.tindalos.guardrails.internal.domain.constraints.labels.Labels;
import org.tindalos.guardrails.internal.domain.core.Node;
import org.tindalos.guardrails.internal.domain.core.Package;

/**
 * Immutable analysis input used by analyzers.
 */
public record AnalysisInput(
        List<Package> packages,
        Set<Node> nodes,
        AnalysisPlan analysisPlan) {

    public AnalysisInput {
        packages = List.copyOf(Objects.requireNonNull(packages, "packages"));
        nodes = Set.copyOf(Objects.requireNonNull(nodes, "nodes"));
        analysisPlan = Objects.requireNonNull(analysisPlan, "analysisPlan");
    }

    public Optional<PackageCouplingConstraints> packageCouplingExpectations() {
        return analysisPlan.constraints().packageCoupling();
    }

    public Optional<ThirdParty> thirdPartyExpectations() {
        return analysisPlan.constraints().thirdParty();
    }

    public Optional<Labels> labels() {
        return analysisPlan.constraints().labels();
    }
}