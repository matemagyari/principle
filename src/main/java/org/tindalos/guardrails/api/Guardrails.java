package org.tindalos.guardrails.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.tindalos.guardrails.internal.app.GuardrailsAnalyser;
import org.tindalos.guardrails.internal.app.reporters.AnalysisResultsReporter;
import org.tindalos.guardrails.internal.infrastructure.constraints.ConstraintReaderRegistry;
import org.tindalos.guardrails.internal.infrastructure.constraints.ConstraintsReader;

/**
 * Public entry point for using Guardrails programmatically.
 * Exposes only minimal operations needed by library clients.
 */
public final class Guardrails {

    private static final String DEFAULT_FILE_LOCATION = "/guardrails.yml";

    private Guardrails() {
    }

    @SuppressWarnings("deprecation")
    public static AnalysisPlan readPlan(Optional<String> fileLocation) {
        return new AnalysisPlan(ConstraintsReader.readFromFile(fileLocation));
    }

    public static Builder builder(String rootPackage) {
        return new Builder(rootPackage);
    }

    @SuppressWarnings("deprecation")
    public static GuardrailsAnalyzer analyzer(String rootPackage) {
        Objects.requireNonNull(rootPackage, "rootPackage");

        GuardrailsAnalyser internalAnalyzer = org.tindalos.guardrails.internal.infrastructure.di.Guardrails
                .createAnalyser(rootPackage);
        AnalysisResultsReporter reporter = org.tindalos.guardrails.internal.infrastructure.di.Guardrails
                .createAggregatedYAMLReporter();

        return plan -> {
            var results = internalAnalyzer.analyze(plan.toInternalPlan());
            return new AnalysisOutcome(results.hasViolations(), reporter.summary(results));
        };
    }

    public static final class Builder {

        private final String rootPackage;
        private final List<Analyzer<? extends AnalysisResult>> customAnalyzers = new ArrayList<>();
        private final List<AnalysisResultReporter<? extends AnalysisResult>> customReporters = new ArrayList<>();
        private final List<ConstraintDefinitionReader<? extends AnalysisResult>> customReaders = new ArrayList<>();

        private Builder(String rootPackage) {
            this.rootPackage = Objects.requireNonNull(rootPackage, "rootPackage");
        }

        public Builder registerAnalyzer(Analyzer<? extends AnalysisResult> analyzer) {
            customAnalyzers.add(Objects.requireNonNull(analyzer, "analyzer"));
            return this;
        }

        public Builder registerReporter(AnalysisResultReporter<? extends AnalysisResult> reporter) {
            customReporters.add(Objects.requireNonNull(reporter, "reporter"));
            return this;
        }

        public Builder registerReader(ConstraintDefinitionReader<? extends AnalysisResult> reader) {
            customReaders.add(Objects.requireNonNull(reader, "reader"));
            return this;
        }

        @SuppressWarnings("deprecation")
        public AnalysisPlan readPlan(Optional<String> fileLocation) {
            var registry = ConstraintReaderRegistry.builder();
            for (var reader : customReaders) {
                registry.register(yamlObj -> reader.read(yamlObj).map(result -> Map.entry(reader.key(), (Object) result)));
            }
            var internalPlan = ConstraintsReader.readFromFile(fileLocation, registry.build());
            return new AnalysisPlan(internalPlan);
        }

        @SuppressWarnings("deprecation")
        public GuardrailsAnalyzer build() {
            var analyzerAdapters = customAnalyzers.stream()
                .map(analyzer -> new ApiAnalyzerAdapter(analyzer))
                .map(adapter -> (org.tindalos.guardrails.internal.domain.analyzers.Analyzer) adapter)
                .toList();

            List<org.tindalos.guardrails.internal.app.reporters.AnalysisResultReporter<?>> reporterAdapters =
                    customReporters.stream()
                            .map(ApiReporterAdapter::new)
                            .map(adapter -> (org.tindalos.guardrails.internal.app.reporters.AnalysisResultReporter<?>) adapter)
                            .collect(Collectors.toList());

            GuardrailsAnalyser internalAnalyzer = org.tindalos.guardrails.internal.infrastructure.di.Guardrails
                .createAnalyser(rootPackage, analyzerAdapters);
            AnalysisResultsReporter internalReporter = org.tindalos.guardrails.internal.infrastructure.di.Guardrails
                .createAggregatedYAMLReporter(reporterAdapters);

            return plan -> {
                var internalResults = internalAnalyzer.analyze(plan.toInternalPlan());
            return new AnalysisOutcome(internalResults.hasViolations(), internalReporter.summary(internalResults));
            };
        }

        private record ApiWrappedResult(AnalysisResult delegate)
                implements org.tindalos.guardrails.internal.domain.core.AnalysisResult {

            @Override
            public boolean constraintViolated() {
                return delegate.constraintViolated();
            }
        }

        private final class ApiAnalyzerAdapter implements org.tindalos.guardrails.internal.domain.analyzers.Analyzer {

            private final Analyzer<? extends AnalysisResult> delegate;

            private ApiAnalyzerAdapter(Analyzer<? extends AnalysisResult> delegate) {
                this.delegate = Objects.requireNonNull(delegate, "delegate");
            }

            @Override
            public org.tindalos.guardrails.internal.domain.core.AnalysisResult analyze(
                    org.tindalos.guardrails.internal.domain.plan.AnalysisInput checkInput) {
                var apiPlan = new AnalysisPlan(checkInput.analysisPlan());
                var result = delegate.analyze(apiPlan);
                return new ApiWrappedResult(result);
            }

            @Override
            public boolean isEnabled(org.tindalos.guardrails.internal.domain.constraints.Constraints constraints) {
                return true;
            }
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        private static final class ApiReporterAdapter
            implements org.tindalos.guardrails.internal.app.reporters.AnalysisResultReporter {

            private final AnalysisResultReporter<? extends AnalysisResult> delegate;

            private ApiReporterAdapter(AnalysisResultReporter<? extends AnalysisResult> delegate) {
                this.delegate = Objects.requireNonNull(delegate, "delegate");
            }

            @Override
            public Class resultType() {
                return ApiWrappedResult.class;
            }

            @Override
            public boolean supports(org.tindalos.guardrails.internal.domain.core.AnalysisResult result) {
                return result instanceof ApiWrappedResult wrapped
                        && delegate.resultType().isInstance(wrapped.delegate());
            }

            @Override
            public String report(org.tindalos.guardrails.internal.domain.core.AnalysisResult result) {
                var wrapped = (ApiWrappedResult) result;
                var typedReporter = (AnalysisResultReporter<AnalysisResult>) delegate;
                return typedReporter.report(wrapped.delegate());
            }
        }
    }
}
