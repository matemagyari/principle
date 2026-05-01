package org.tindalos.guardrails.internal.infrastructure.reporters.packagestructure;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Map;

import org.tindalos.guardrails.internal.domain.analyzers.structure.CohesionAnalysisResult;
import org.tindalos.guardrails.internal.domain.analyzers.structure.NodeGroup;
import org.tindalos.guardrails.internal.infrastructure.reporters.ReportsDirectoryManager;
import static org.tindalos.guardrails.internal.infrastructure.reporters.packagestructure.PackageCohesionConstants.GENERAL_DESCRIPTION;
import static org.tindalos.guardrails.internal.infrastructure.reporters.packagestructure.PackageCohesionConstants.PACKAGE_COHESIONS_FILE_NAME;
import static org.tindalos.guardrails.internal.infrastructure.reporters.packagestructure.PackageCohesionConstants.SECTION_LINE;
import static org.tindalos.guardrails.internal.infrastructure.reporters.packagestructure.PackageCohesionConstants.round;

/**
 * Writes package cohesion analysis results for existing packages to a plain text report.
 */
final class ExistingPackageCohesionsFileWriter {

    private static final String COLUMNS = """
            | Cohesion | Size | Upstream/Downstream dependencies of package | Internal/External edges of the classes |
            """;
    private static final String ONE_ELEMENT_PACKAGE_DESCRIPTION =
            "Each of the following packages contains only one class, therefore no cohesion is calculated";
    private static final String REPORT_HEADER = """

            %s
            Package cohesions - existing packages are listed, ordered by cohesion
            %s

            """;

    private ExistingPackageCohesionsFileWriter() {
    }

        static void writeToFile(CohesionAnalysisResult result) {
        try (var printWriter = new PrintWriter(ReportsDirectoryManager.reportDirectoryPath + "/" + PACKAGE_COHESIONS_FILE_NAME)) {
            printWriter
                    .append(GENERAL_DESCRIPTION)
                    .append(REPORT_HEADER.formatted(SECTION_LINE, SECTION_LINE))
                    .append(COLUMNS);

            result.packages().entrySet().stream()
                    .filter(entry -> entry.getValue().nodes().size() > 1)
                    .sorted(Comparator.comparingDouble((Map.Entry<String, NodeGroup> entry) -> entry.getValue().cohesion()).reversed())
                    .forEach(entry -> printWriter.append("\n " + groupToLine(entry.getValue()) + "\t" + entry.getKey()));

            printWriter.append("\n" + SECTION_LINE + "\n");
            var singleClassPackages = result.packages().entrySet().stream()
                    .filter(entry -> entry.getValue().nodes().size() == 1)
                    .toList();
            printWriter.append("""

                    %s (%d)

                    """.formatted(ONE_ELEMENT_PACKAGE_DESCRIPTION, singleClassPackages.size()));
            singleClassPackages.stream()
                    .map(entry -> entry.getValue().nodes().iterator().next().id())
                    .sorted()
                    .forEach(nodeId -> printWriter.append(nodeId).append("\n"));
        } catch (FileNotFoundException exception) {
            throw new RuntimeException("Failed to write existing package cohesions report to " +
                    ReportsDirectoryManager.reportDirectoryPath + "/" + PACKAGE_COHESIONS_FILE_NAME, exception);
        }
    }

    private static String groupToLine(NodeGroup nodeGroup) {
        return """
                %s	| %d	| %d/%d	| %d/%d |
                """
                .formatted(
                        round(nodeGroup.cohesion()),
                        nodeGroup.nodes().size(),
                        nodeGroup.externalDependants.size(),
                        nodeGroup.externalDependencies.size(),
                        nodeGroup.internalArcsNo,
                        nodeGroup.externalArcsNo
                )
                .stripTrailing();
    }
}