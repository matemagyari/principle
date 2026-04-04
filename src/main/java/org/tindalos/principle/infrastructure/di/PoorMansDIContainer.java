package org.tindalos.principle.infrastructure.di;

import java.util.List;

import org.tindalos.principle.app.AnalysisPlanValidatorImpl;
import org.tindalos.principle.app.NodeBuilder;
import org.tindalos.principle.app.PrincipleApplication;
import org.tindalos.principle.domain.AnalysisRunner;
import org.tindalos.principle.domain.AnalysisRunnerImpl;
import org.tindalos.principle.domain.analyzers.Analyzer;
import org.tindalos.principle.domain.analyzers.acd.ComponentDependenciesAnalyzer;
import org.tindalos.principle.domain.analyzers.adp.CycleDetector;
import org.tindalos.principle.domain.analyzers.layering.LayerViolationAnalyzer;
import org.tindalos.principle.domain.analyzers.sap.SAPViolationAnalyzer;
import org.tindalos.principle.domain.analyzers.sdp.SDPViolationAnalyzer;
import org.tindalos.principle.domain.analyzers.structure.PackageCohesionAnalyzer;
import org.tindalos.principle.domain.analyzers.submodulesblueprint.SubmodulesBlueprintAnalyzer;
import org.tindalos.principle.domain.analyzers.submodulesblueprint.SubmodulesBuilder;
import org.tindalos.principle.domain.analyzers.thirdparty.ThirdPartyAnalyzer;
import org.tindalos.principle.domain.core.PackageStructureBuilder;
import org.tindalos.principle.app.reporters.AnalysisResultsReporter;
import org.tindalos.principle.infrastructure.JDependBasedPackageListBuilder;
import org.tindalos.principle.infrastructure.PackageStructureBuilderImpl;
import org.tindalos.principle.infrastructure.reporters.YAMLADPAnalysisResultReporter;
import org.tindalos.principle.infrastructure.reporters.YAMLComponentDependencyAnalysisResultReporter;
import org.tindalos.principle.infrastructure.reporters.YAMLLayerAnalysisResultReporter;
import org.tindalos.principle.infrastructure.reporters.YAMLSAPAnalysisResultReporter;
import org.tindalos.principle.infrastructure.reporters.YAMLSDPAnalysisResultReporter;
import org.tindalos.principle.infrastructure.reporters.YAMLSubmodulesBlueprintAnalysisResultReporter;
import org.tindalos.principle.infrastructure.reporters.YAMLThirdPartyAnalysisResultReporter;
import org.tindalos.principle.infrastructure.reporters.packagestructure.YAMLPackageCohesionAnalysisResultReporter;
import org.tindalos.principle.infrastructure.service.jdepend.classdependencies.DefaultNodeBuilder;

/**
 * Manual dependency wiring for runtime and tests.
 */
public final class PoorMansDIContainer {

    private PoorMansDIContainer() {
    }

    public static PrincipleApplication buildAnalyzer(String rootPackage) {
        NodeBuilder nodeBuilder = new DefaultNodeBuilder();

        AnalysisRunner analysisRunner = buildAnalysisRunner();

        return new PrincipleApplication(
                new AnalysisPlanValidatorImpl(),
                new JDependBasedPackageListBuilder(rootPackage),
                nodeBuilder,
                analysisRunner);
    }

    public static AnalysisResultsReporter createReporter() {
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

    public static AnalysisRunner buildAnalysisRunner() {
        PackageStructureBuilder packageStructureBuilder = new PackageStructureBuilderImpl();
        return new AnalysisRunnerImpl(createAnalyzers(packageStructureBuilder));
    }

    private static List<Analyzer> createAnalyzers(PackageStructureBuilder packageStructureBuilder) {
        return List.of(
                new LayerViolationAnalyzer(),
                new ThirdPartyAnalyzer(),
                new CycleDetector(packageStructureBuilder),
                new SDPViolationAnalyzer(),
                new SAPViolationAnalyzer(),
                new ComponentDependenciesAnalyzer(packageStructureBuilder),
                new SubmodulesBlueprintAnalyzer(new SubmodulesBuilder(packageStructureBuilder)),
                new PackageCohesionAnalyzer());
    }
}
