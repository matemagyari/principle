package org.tindalos.principle.infrastructure.reporters.packagestructure;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.stream.Collectors;

import org.tindalos.principle.domain.analyzers.structure.GroupingResult;
import org.tindalos.principle.infrastructure.reporters.ReportsDirectoryManager;
import static org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionConstants.GRAPH_DESCRIPTION;
import static org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionConstants.PACKAGE_STRUCTURE_HINTS1_FILE_NAME;
import static org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionConstants.SUB_SECTION_LINE;

/**
 * Writes source-based package structure hints to a plain text report.
 */
final class PackageStructureHints1FileWriter {

    private static final String DESCRIPTION = """
            In the first step the algorithm finds the "source" vertices in the graph, the classes that are on the top of the dependency hierarchy (no other class refers to them).The second step groups all the classes in the graph based on sources as upstream dependencies. This gives some good package structuring hints. E.g.

            Sources: s01, s02
	org.home.sample.Person
	org.home.sample.Role

            means the Person and Role classes are downstream dependencies of s01 and s02 but not of the other sources.
            """;

    private PackageStructureHints1FileWriter() {
    }

        static void writeToFile(GroupingResult grouping) {
        try (var printWriter = new PrintWriter(ReportsDirectoryManager.reportDirectoryPath + "/" + PACKAGE_STRUCTURE_HINTS1_FILE_NAME)) {
            printWriter
                    .append(GRAPH_DESCRIPTION)
                    .append("\n\n")
                    .append(DESCRIPTION)
                    .append("\n\n")
                    .append("\nSources (" + grouping.labelledSources().size() + ")\n")
                    .append(SUB_SECTION_LINE)
                    .append("\n\n");

            grouping.labelledSources().stream()
                    .sorted(Comparator.comparing(GroupingResult.LabelledSource::label))
                    .forEach(source -> printWriter.append(source.label()).append(" -> ").append(source.nodeId()).append("\n"));

            printWriter
                    .append("\nGroups (" + grouping.grouping().size() + ") ordered by size\n")
                    .append(SUB_SECTION_LINE)
                    .append("\n\n");

            grouping.grouping().entrySet().stream()
                    .sorted(Comparator.comparingInt(entry -> entry.getValue().size()))
                    .forEach(entry -> {
                        var sources = entry.getKey().stream().collect(Collectors.joining(","));
                        printWriter.append("Sources: ").append(sources).append("\n");
                        entry.getValue().stream()
                                .sorted()
                                .forEach(value -> printWriter.append("\t").append(value).append("\n"));
                    });
        } catch (FileNotFoundException exception) {
            throw new RuntimeException("Failed to write package structure hints report to " +
                    ReportsDirectoryManager.reportDirectoryPath + "/" + PACKAGE_STRUCTURE_HINTS1_FILE_NAME, exception);
        }
    }
}