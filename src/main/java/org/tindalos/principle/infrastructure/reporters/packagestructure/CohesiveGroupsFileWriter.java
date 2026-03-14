package org.tindalos.principle.infrastructure.reporters.packagestructure;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import org.tindalos.principle.domain.analyzers.structure.Node;
import org.tindalos.principle.domain.analyzers.structure.NodeGroup;
import org.tindalos.principle.infrastructure.reporters.ReportsDirectoryManager;
import static org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionConstants.COHESIVE_GROUPS_FILE_NAME;
import static org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionConstants.GENERAL_DESCRIPTION;
import static org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionConstants.SECTION_LINE;
import static org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionConstants.SUB_SECTION_LINE;
import static org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionConstants.round;

/**
 * Writes cohesive node groups to a plain text report.
 */
public final class CohesiveGroupsFileWriter {

    private static final String COHESIVE_GROUPS_DESCRIPTION =
            "The following groups are formed as a result of cohesion analysis";
    private static final String ORPHAN_NODES_DESCRIPTION =
            "\nThe following classes did not fit into any cohesive group";

    private CohesiveGroupsFileWriter() {
    }

    public static void writeToFile(Set<NodeGroup> cohesiveNodeGroups) {
        try (var printWriter = new PrintWriter(ReportsDirectoryManager.reportDirectoryPath + "/" + COHESIVE_GROUPS_FILE_NAME)) {
            printWriter
                    .append(GENERAL_DESCRIPTION)
                    .append("\n\n")
                    .append(COHESIVE_GROUPS_DESCRIPTION)
                    .append("\n")
                    .append(SECTION_LINE)
                    .append("\n\n");

            cohesiveNodeGroups.stream()
                    .sorted(Comparator.comparingDouble(NodeGroup::cohesion).reversed())
                    .filter(group -> group.nodes().size() > 1)
                    .forEach(group -> printWriter
                            .append("\n")
                            .append(groupToLine(group))
                            .append(" \n")
                            .append(listNodes(group))
                            .append(" \n")
                            .append(SUB_SECTION_LINE)
                            .append("\n"));

            printWriter.append("\n").append(SECTION_LINE).append("\n");

            var orphanNodes = cohesiveNodeGroups.stream()
                    .filter(group -> group.nodes().size() == 1)
                    .toList();
            printWriter
                    .append(ORPHAN_NODES_DESCRIPTION)
                    .append(" (")
                    .append(String.valueOf(orphanNodes.size()))
                    .append(")\n\n");
            orphanNodes.stream()
                    .map(group -> group.nodes().iterator().next().id())
                    .sorted()
                    .forEach(nodeId -> printWriter.append(nodeId).append("\n"));
        } catch (FileNotFoundException exception) {
            throw new RuntimeException("Failed to write cohesive groups report to " +
                    ReportsDirectoryManager.reportDirectoryPath + "/" + COHESIVE_GROUPS_FILE_NAME, exception);
        }
    }

    private static String listNodes(NodeGroup group) {
        return group.nodes().stream()
                .map(Node::id)
                .sorted()
                .collect(Collectors.joining("\n", "\n", ""));
    }

    private static String groupToLine(NodeGroup group) {
        return """
                Cohesion: %s | Size: %d | Upstream/Downstream dependencies of the group : %d/%d | Internal/External edges of the classes: %d/%d |
                """
                .formatted(
                        round(group.cohesion()),
                        group.nodes().size(),
                        group.externalDependants.size(),
                        group.externalDependencies.size(),
                        group.internalArcsNo,
                        group.externalArcsNo
                )
                .stripTrailing();
    }
}