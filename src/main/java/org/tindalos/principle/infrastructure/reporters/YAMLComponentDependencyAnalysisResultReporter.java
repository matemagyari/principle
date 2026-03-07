package org.tindalos.principle.infrastructure.reporters;

import org.tindalos.principle.app.reporters.ComponentDependencyAnalysisResultReporter;
import org.tindalos.principle.domain.analyzers.acd.ComponentDependenciesResult;

/**
 * Reports Component Dependency analysis results in YAML format.
 * Produces a structured, machine-readable representation of ACD/RACD/NCCD metrics
 * and their violation status, suitable for further processing or integration with other tools.
 */
public class YAMLComponentDependencyAnalysisResultReporter implements ComponentDependencyAnalysisResultReporter {

    @Override
    public String report(ComponentDependenciesResult result) {
        var sb = new StringBuilder();
        sb.append("component_dependency_result:\n");
        sb.append("  description: Average Component Dependency constraint\n");
        sb.append("  constraint_violated: ").append(result.constraintViolated()).append("\n");
        sb.append("  metrics:\n");
        sb.append("    acd: ").append(result.acd()).append("\n");
        sb.append("    racd: ").append(result.rAcd()).append("\n");
        sb.append("    nccd: ").append(result.nCcd()).append("\n");
        sb.append("    num_of_components: ").append(result.numOfComponents()).append("\n");

        result.packageCoupling().racd().ifPresentOrElse(
                racd -> sb.append("  racd_threshold: ").append(racd.threshold()).append("\n"),
                () ->   sb.append("  racd_threshold: ~\n"));

        result.packageCoupling().nccd().ifPresentOrElse(
                nccd -> sb.append("  nccd_threshold: ").append(nccd.threshold()).append("\n"),
                () ->   sb.append("  nccd_threshold: ~\n"));

        return sb.toString();
    }
}

