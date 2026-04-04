package org.tindalos.principle.infrastructure.reporters.packagestructure;

import org.tindalos.principle.domain.analyzers.structure.CohesionAnalysisResult;
import org.tindalos.principle.app.reporters.PackageCohesionAnalysisResultReporter;
import org.tindalos.principle.infrastructure.reporters.ReportsDirectoryManager;
import static org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionConstants.COHESIVE_GROUPS_FILE_NAME;
import static org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionConstants.PACKAGE_COHESIONS_FILE_NAME;
import static org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionConstants.PACKAGE_STRUCTURE_HINTS1_FILE_NAME;
import static org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionConstants.PACKAGE_STRUCTURE_HINTS2_FILE_NAME;
import static org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionConstants.SECTION_LINE;

/**
 * Reports package cohesion analysis results and writes detail files.
 */
public final class PlainEnglishPackageCohesionAnalysisResultReporter implements PackageCohesionAnalysisResultReporter {

    @Override
    public String report(CohesionAnalysisResult result) {
        String fileNames = PACKAGE_COHESIONS_FILE_NAME + ", " +
                PACKAGE_STRUCTURE_HINTS1_FILE_NAME + ", " +
                PACKAGE_STRUCTURE_HINTS2_FILE_NAME;

        ExistingPackageCohesionsFileWriter.writeToFile(result);
        PackageStructureHints1FileWriter.writeToFile(result.groupingResult());
        PackageStructureHints2FileWriter.writeToFile(result.subgraphDecomposition());

        if (result.cohesiveNodeGroups().isPresent()) {
            CohesiveGroupsFileWriter.writeToFile(result.cohesiveNodeGroups().get());
            fileNames += ", " + COHESIVE_GROUPS_FILE_NAME;
        }

        var sb = new StringBuilder("\n" + SECTION_LINE + "\n");
        sb.append("\tPackage Cohesion Analysis\t");
        sb.append("\n").append(SECTION_LINE).append("\n");
        sb.append("\nFor details check files: ")
                .append(fileNames)
                .append(" in ")
                .append(ReportsDirectoryManager.ensureReportsDirectoryExists())
                .append("\n\n");
        sb.append(SECTION_LINE).append("\n");

        return sb.toString();
    }
}
