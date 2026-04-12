package org.tindalos.principle.infrastructure.reporters.packagestructure;

import org.tindalos.principle.domain.analyzers.structure.CohesionAnalysisResult;
import org.tindalos.principle.domain.analyzers.structure.NodeGroup;
import org.tindalos.principle.app.reporters.PackageCohesionAnalysisResultReporter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

import static org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionConstants.COHESIVE_GROUPS_FILE_NAME;
import static org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionConstants.PACKAGE_COHESIONS_FILE_NAME;
import static org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionConstants.PACKAGE_STRUCTURE_HINTS1_FILE_NAME;
import static org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionConstants.PACKAGE_STRUCTURE_HINTS2_FILE_NAME;
import static org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionConstants.round;

/**
 * Reports package cohesion analysis results in YAML format.
 * Produces a structured, machine-readable representation of each package's cohesion metrics.
 */
public final class YAMLPackageCohesionAnalysisResultReporter implements PackageCohesionAnalysisResultReporter {

    @Override
    public String report(CohesionAnalysisResult result) {
        ExistingPackageCohesionsFileWriter.writeToFile(result);
        PackageStructureHints1FileWriter.writeToFile(result.groupingResult());
        PackageStructureHints2FileWriter.writeToFile(result.subgraphDecomposition());
        result.cohesiveNodeGroups().ifPresent(CohesiveGroupsFileWriter::writeToFile);

        return """
            package_cohesion_result:
              description: Package Cohesion Analysis
              package_count: %d
            """.formatted(result.packages().size()) +
                filesYaml(result) +
                packagesYaml(result);
    }

    private String filesYaml(CohesionAnalysisResult result) {
        var files = new ArrayList<String>();
        files.add(PACKAGE_COHESIONS_FILE_NAME);
        files.add(PACKAGE_STRUCTURE_HINTS1_FILE_NAME);
        files.add(PACKAGE_STRUCTURE_HINTS2_FILE_NAME);
        if (result.cohesiveNodeGroups().isPresent()) {
            files.add(COHESIVE_GROUPS_FILE_NAME);
        }

        var sb = new StringBuilder("  detail_files:\n");
        files.forEach(file -> sb.append("    - ").append(file).append("\n"));
        return sb.toString();
    }

    private String packagesYaml(CohesionAnalysisResult result) {
        if (result.packages().isEmpty()) {
            return "  packages: []\n";
        }

        var lines = result.packages().entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(this::entryToYaml)
                .reduce("", String::concat);
        return "  packages:\n" + lines;
    }

    private String entryToYaml(Map.Entry<String, NodeGroup> entry) {
        return "    - name: " + entry.getKey() + "\n" +
                "      cohesion: " + round(entry.getValue().cohesion()) + "\n" +
                "      size: " + entry.getValue().nodes().size() + "\n";
    }
}
