package org.tindalos.principle.internal.infrastructure.reporters;

import org.tindalos.principle.internal.app.reporters.ComponentDependencyAnalysisResultReporter;
import org.tindalos.principle.internal.domain.analyzers.acd.ComponentDependenciesResult;

/**
 * Reports Component Dependency analysis results in YAML format.
 * Produces a structured, machine-readable representation of ACD/RACD/NCCD metrics
 * and their violation status, suitable for further processing or integration with other tools.
 */
public class YAMLComponentDependencyAnalysisResultReporter implements ComponentDependencyAnalysisResultReporter {

    @Override
    public String report(ComponentDependenciesResult result) {
        return """
                component_dependency_result:
                  description: Average Component Dependency constraint
                  constraint_violated: %s
                  metrics:
                    component_dependency:
                      average: %s
                      relative_average: %s
                      normalized_cumulative: %s
                    num_of_components: %s
                  racd_threshold: %s
                  nccd_threshold: %s
                """.formatted(
                result.constraintViolated(),
                result.acd(),
                result.rAcd(),
                result.nCcd(),
                result.numOfComponents(),
                result.packageCoupling().racd().map(r -> String.valueOf(r.threshold())).orElse("~"),
                result.packageCoupling().nccd().map(n -> String.valueOf(n.threshold())).orElse("~"));
    }
}

