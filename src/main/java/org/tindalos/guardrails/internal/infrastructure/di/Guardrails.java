package org.tindalos.guardrails.internal.infrastructure.di;

import java.util.List;

import org.tindalos.guardrails.internal.app.AnalysisPlanValidatorImpl;
import org.tindalos.guardrails.internal.app.GuardrailsAnalyser;
import org.tindalos.guardrails.internal.app.reporters.AnalysisResultsReporter;
import org.tindalos.guardrails.internal.domain.AnalysisRunner;
import org.tindalos.guardrails.internal.domain.AnalysisRunnerImpl;
import org.tindalos.guardrails.internal.domain.analyzers.acd.ComponentDependenciesAnalyzer;
import org.tindalos.guardrails.internal.domain.analyzers.adp.CycleDetector;
import org.tindalos.guardrails.internal.domain.analyzers.layering.LayerViolationAnalyzer;
import org.tindalos.guardrails.internal.domain.analyzers.sap.SAPViolationAnalyzer;
import org.tindalos.guardrails.internal.domain.analyzers.sdp.SDPViolationAnalyzer;
import org.tindalos.guardrails.internal.domain.analyzers.structure.PackageCohesionAnalyzer;
import org.tindalos.guardrails.internal.domain.analyzers.submodulesblueprint.SubmodulesBlueprintAnalyzer;
import org.tindalos.guardrails.internal.domain.analyzers.submodulesblueprint.SubmodulesBuilder;
import org.tindalos.guardrails.internal.domain.analyzers.thirdparty.ThirdPartyAnalyzer;
import org.tindalos.guardrails.internal.domain.core.PackageStructureBuilder;
import org.tindalos.guardrails.internal.infrastructure.reporters.YAMLADPAnalysisResultReporter;
import org.tindalos.guardrails.internal.infrastructure.reporters.YAMLComponentDependencyAnalysisResultReporter;
import org.tindalos.guardrails.internal.infrastructure.reporters.YAMLLayerAnalysisResultReporter;
import org.tindalos.guardrails.internal.infrastructure.reporters.YAMLSAPAnalysisResultReporter;
import org.tindalos.guardrails.internal.infrastructure.reporters.YAMLSDPAnalysisResultReporter;
import org.tindalos.guardrails.internal.infrastructure.reporters.YAMLSubmodulesBlueprintAnalysisResultReporter;
import org.tindalos.guardrails.internal.infrastructure.reporters.YAMLThirdPartyAnalysisResultReporter;
import org.tindalos.guardrails.internal.infrastructure.reporters.packagestructure.YAMLPackageCohesionAnalysisResultReporter;
import org.tindalos.guardrails.internal.infrastructure.service.jdepend.JDependBasedPackageListBuilder;
import org.tindalos.guardrails.internal.infrastructure.service.jdepend.classdependencies.DefaultNodeBuilder;

/**
 * Manual dependency wiring for runtime and tests.
 */
public final class Guardrails {

    private Guardrails() {
    }

    public static GuardrailsAnalyser createAnalyser(String rootPackage) {
        return new GuardrailsAnalyser(
                new AnalysisPlanValidatorImpl(),
                new JDependBasedPackageListBuilder(rootPackage),
                new DefaultNodeBuilder(),
                createAnalysisRunner());
    }

    public static AnalysisResultsReporter createAggregatedYAMLReporter() {
        return new AnalysisResultsReporter(
                new YAMLADPAnalysisResultReporter(),
                new YAMLLayerAnalysisResultReporter(),
                new YAMLThirdPartyAnalysisResultReporter(),
                new YAMLSAPAnalysisResultReporter(),
                new YAMLComponentDependencyAnalysisResultReporter(),
                new YAMLSubmodulesBlueprintAnalysisResultReporter(),
                new YAMLSDPAnalysisResultReporter(),
                new YAMLPackageCohesionAnalysisResultReporter());
    }

    private static AnalysisRunner createAnalysisRunner() {
        PackageStructureBuilder packageStructureBuilder = new PackageStructureBuilderImpl();
        return new AnalysisRunnerImpl(List.of(
                new LayerViolationAnalyzer(),
                new ThirdPartyAnalyzer(),
                new CycleDetector(packageStructureBuilder),
                new SDPViolationAnalyzer(),
                new SAPViolationAnalyzer(),
                new ComponentDependenciesAnalyzer(packageStructureBuilder),
                new SubmodulesBlueprintAnalyzer(new SubmodulesBuilder(packageStructureBuilder)),
                new PackageCohesionAnalyzer()));
    }

}
