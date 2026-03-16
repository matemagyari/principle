package org.tindalos.principle.domain;

import org.tindalos.principle.domain.analyzers.Analyzer;
import org.tindalos.principle.utils.logging.TheLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        var results = new ArrayList<AnalysisResult>();
        for (var analyzer : analyzers) {
            if (analyzer.isEnabled(input.analysisPlan().constraints())) {
                runAnalyzer(input, analyzer).ifPresent(results::add);
            }
        }
        return results;
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
