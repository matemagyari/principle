package org.tindalos.principle.infrastructure.reporters.packagestructure;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Comparator;

import org.tindalos.principle.domain.analyzers.structure.NodeGroup;
import org.tindalos.principle.domain.analyzers.structure.Peninsula;
import org.tindalos.principle.domain.analyzers.structure.SubgraphDecomposition;
import org.tindalos.principle.domain.core.Node;
import org.tindalos.principle.infrastructure.reporters.ReportsDirectoryManager;
import static org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionConstants.GRAPH_DESCRIPTION;
import static org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionConstants.PACKAGE_STRUCTURE_HINTS2_FILE_NAME;
import static org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionConstants.SUB_SECTION_LINE;

/**
 * Writes detachable subgraph based package structure hints to a plain text report.
 */
final class PackageStructureHints2FileWriter {

    private static final String DESCRIPTION = """
            The algorithm finds the classes in the codebase (vertices in the graph) that "isolate" some classes from the rest. Only the "top" class is referred anywhere else, meaning that the group could be "collapsed" into the top class without any external change needed. This gives some good package structuring hints (e.g. a group can be put in one package with package private visibility for all classes but the "top"). A group can be a subset of another. "Islands", groups of interconnected classes having no external upstream or downstream dependencies, are also identified.
            The cohesion value is also calculated for these groups.
            """;

    private PackageStructureHints2FileWriter() {
    }

    static void writeToFile(SubgraphDecomposition subgraphDecomposition) {
        try (var printWriter = new PrintWriter(ReportsDirectoryManager.reportDirectoryPath + "/" + PACKAGE_STRUCTURE_HINTS2_FILE_NAME)) {
            printWriter
                    .append(GRAPH_DESCRIPTION)
                    .append("\n\n")
                    .append(DESCRIPTION)
                    .append("\n\n")
                    .append(SUB_SECTION_LINE)
                    .append("\n\n");

            subgraphDecomposition.peninsulas().stream()
                    .sorted(Comparator.comparingInt((Peninsula peninsula) -> peninsula.subgraph().size()).reversed())
                    .forEach(peninsula -> printPeninsula(printWriter, peninsula));
        } catch (FileNotFoundException exception) {
            throw new RuntimeException("Failed to write package structure hints report to " +
                    ReportsDirectoryManager.reportDirectoryPath + "/" + PACKAGE_STRUCTURE_HINTS2_FILE_NAME, exception);
        }
    }

    private static void printPeninsula(PrintWriter printWriter, Peninsula peninsula) {
        var firstLine = "\nCohesion: " + new NodeGroup(peninsula.subgraph()).cohesion();
        if (peninsula.island()) {
            printWriter.append(firstLine).append(" - This is an island\n");
        } else {
            printWriter.append(firstLine).append("\n");
        }

        peninsula.frontNodes().stream()
                .map(Node::id)
                .sorted()
                .forEach(nodeId -> printWriter.append("Top class: ").append(nodeId).append("\n"));

        printWriter.append(SUB_SECTION_LINE).append("\n");

        peninsula.subgraph().stream()
                .filter(node -> !peninsula.frontNodes().contains(node))
                .map(Node::id)
                .sorted()
                .forEach(nodeId -> printWriter.append("           ").append(nodeId).append("\n"));

        printWriter.append("\n");
    }
}