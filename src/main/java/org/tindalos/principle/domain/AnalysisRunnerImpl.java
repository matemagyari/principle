package org.tindalos.principle.domain;

import java.util.List;
import java.util.Optional;

import org.tindalos.principle.domain.analyzers.Analyzer;
import org.tindalos.principle.utils.logging.TheLogger;

/**
 * Default implementation of {@link AnalysisRunner} that runs all enabled analyzers sequentially.
 */
public class AnalysisRunnerImpl implements AnalysisRunner {

    private final List<Analyzer> analyzers;

    public AnalysisRunnerImpl(List<Analyzer> analyzers) {
        this.analyzers = analyzers;
    }

    @Override
    public List<AnalysisResult> run(AnalysisInput input) {
        var constraints = input.analysisPlan().constraints();
        return analyzers.stream()
                .filter(analyzer -> analyzer.isEnabled(constraints))
                .map(analyzer -> runAnalyzer(input, analyzer))
                .flatMap(Optional::stream)
                .toList();
    }

    private Optional<AnalysisResult> runAnalyzer(AnalysisInput input, Analyzer analyzer) {
        try {
            TheLogger.info(analyzer + " is running.");
            return Optional.of(analyzer.analyze(input));
        } catch (RuntimeException e) {
            TheLogger.error(e.getMessage());
            return Optional.empty();
        }
    }
}
