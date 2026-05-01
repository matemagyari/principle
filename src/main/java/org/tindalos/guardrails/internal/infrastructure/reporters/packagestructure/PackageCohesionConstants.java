package org.tindalos.guardrails.internal.infrastructure.reporters.packagestructure;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Shared constants and utilities for package cohesion reporters and file writers.
 */
final class PackageCohesionConstants {

    static final String SECTION_LINE     = "================================================================================";
    static final String SUB_SECTION_LINE = "-----------------------------------------------------------------------------";

    static final String COHESIVE_GROUPS_FILE_NAME          = "identified_cohesive_groups.txt";
    static final String PACKAGE_COHESIONS_FILE_NAME        = "existing_packages_cohesion.txt";
    static final String PACKAGE_STRUCTURE_HINTS1_FILE_NAME = "code_structure_observations1.txt";
    static final String PACKAGE_STRUCTURE_HINTS2_FILE_NAME = "code_structure_observations2.txt";

    static final String GRAPH_DESCRIPTION =
            "A directed graph is built representing the structure of the code, " +
            "where each class appears as a vertex and each relationship between classes (composition, inheritance, ...) " +
            "as a directed edge between the corresponding two vertices.";

    static final String GENERAL_DESCRIPTION =
            GRAPH_DESCRIPTION +
            " Cohesion between a group of vertices (classes) is calculated by the " +
            "\n\n\tC = 1 - E1 / E2 " +
            "\n\nformula. E1 is the number of edges the vertices in the group participate in. This means 'internal' edges, where both ends of the edge is from the group and 'external' ones, where only one end is. " +
            " E2 would be the number of edges belonging to the new vertex if the vertices in the group collapsed into one. So all internal edges would disappear and multiple external edges might collapse into each other as well. " +
            "\nThe cohesion measures how much relative decrease in the number of edges would a grouping of a given set of vertices cause. 0.0 means the collapsing wouldn't decrease the number of edges at all, while 1 means would be no edge left.";

    static double round(double d) {
        return new BigDecimal(d).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
