package org.tindalos.guardrails.internal.infrastructure.di;

import java.util.List;

import org.tindalos.guardrails.internal.app.AnalysisPlanValidatorImpl;
import org.tindalos.guardrails.internal.app.GuardrailsAnalyser;
import org.tindalos.guardrails.internal.app.reporters.AnalysisResultsReporter;
import org.tindalos.guardrails.internal.domain.AnalysisRunner;
import org.tindalos.guardrails.internal.domain.AnalysisRunnerImpl;
import org.tindalos.guardrails.internal.domain.analyzers.Analyzer;
import org.tindalos.guardrails.internal.domain.analyzers.acd.ComponentDependenciesAnalyzer;
import org.tindalos.guardrails.internal.domain.analyzers.adp.CycleDetector;
import org.tindalos.guardrails.internal.domain.analyzers.sap.SAPViolationAnalyzer;
import org.tindalos.guardrails.internal.domain.analyzers.sdp.SDPViolationAnalyzer;
import org.tindalos.guardrails.internal.domain.analyzers.structure.PackageCohesionAnalyzer;
import org.tindalos.guardrails.internal.domain.analyzers.labels.LabelsAnalyzer;
import org.tindalos.guardrails.internal.domain.analyzers.labels.LabelsBuilder;
import org.tindalos.guardrails.internal.domain.analyzers.thirdparty.ThirdPartyAnalyzer;
import org.tindalos.guardrails.internal.domain.core.PackageStructureBuilder;
import org.tindalos.guardrails.internal.infrastructure.reporters.YAMLADPAnalysisResultReporter;
import org.tindalos.guardrails.internal.infrastructure.reporters.YAMLComponentDependencyAnalysisResultReporter;
import org.tindalos.guardrails.internal.infrastructure.reporters.YAMLSAPAnalysisResultReporter;
import org.tindalos.guardrails.internal.infrastructure.reporters.YAMLSDPAnalysisResultReporter;
import org.tindalos.guardrails.internal.infrastructure.reporters.YAMLLabelsAnalysisResultReporter;
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
        return createAnalyser(rootPackage, List.of());
        }

        public static GuardrailsAnalyser createAnalyser(String rootPackage, List<Analyzer> additionalAnalyzers) {
        return new GuardrailsAnalyser(
                new AnalysisPlanValidatorImpl(),
                new JDependBasedPackageListBuilder(rootPackage),
                new DefaultNodeBuilder(),
            createAnalysisRunner(additionalAnalyzers));
    }

    public static AnalysisResultsReporter createAggregatedYAMLReporter() {
        return createAggregatedYAMLReporter(List.of());
        }

        public static AnalysisResultsReporter createAggregatedYAMLReporter(
            List<org.tindalos.guardrails.internal.app.reporters.AnalysisResultReporter<?>> additionalReporters) {
        var builtIn = List.of(
                new YAMLADPAnalysisResultReporter(),
                new YAMLThirdPartyAnalysisResultReporter(),
                new YAMLSAPAnalysisResultReporter(),
                new YAMLComponentDependencyAnalysisResultReporter(),
                new YAMLLabelsAnalysisResultReporter(),
                new YAMLSDPAnalysisResultReporter(),
            new YAMLPackageCohesionAnalysisResultReporter());

        var all = new java.util.ArrayList<org.tindalos.guardrails.internal.app.reporters.AnalysisResultReporter<?>>();
        all.addAll(builtIn);
        all.addAll(additionalReporters);
        return new AnalysisResultsReporter(all);
    }

        private static AnalysisRunner createAnalysisRunner(List<Analyzer> additionalAnalyzers) {
        PackageStructureBuilder packageStructureBuilder = new PackageStructureBuilderImpl();
        var builtIn = List.of(
                new ThirdPartyAnalyzer(),
                new CycleDetector(packageStructureBuilder),
                new SDPViolationAnalyzer(),
                new SAPViolationAnalyzer(),
                new ComponentDependenciesAnalyzer(packageStructureBuilder),
                new LabelsAnalyzer(new LabelsBuilder(packageStructureBuilder)),
            new PackageCohesionAnalyzer());

        var all = new java.util.ArrayList<Analyzer>();
        all.addAll(builtIn);
        all.addAll(additionalAnalyzers);
        return new AnalysisRunnerImpl(all);
    }

}
