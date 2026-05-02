package org.tindalos.guardrails.internal.infrastructure.reporters.packagestructure;

import org.tindalos.guardrails.internal.app.reporters.PackageCohesionAnalysisResultReporter;
import org.tindalos.guardrails.internal.domain.analyzers.structure.CohesionAnalysisResult;
import org.tindalos.guardrails.internal.infrastructure.reporters.ReportsDirectoryManager;
import static org.tindalos.guardrails.internal.infrastructure.reporters.packagestructure.PackageCohesionConstants.COHESIVE_GROUPS_FILE_NAME;
import static org.tindalos.guardrails.internal.infrastructure.reporters.packagestructure.PackageCohesionConstants.PACKAGE_COHESIONS_FILE_NAME;
import static org.tindalos.guardrails.internal.infrastructure.reporters.packagestructure.PackageCohesionConstants.PACKAGE_STRUCTURE_HINTS1_FILE_NAME;
import static org.tindalos.guardrails.internal.infrastructure.reporters.packagestructure.PackageCohesionConstants.PACKAGE_STRUCTURE_HINTS2_FILE_NAME;
import static org.tindalos.guardrails.internal.infrastructure.reporters.packagestructure.PackageCohesionConstants.SECTION_LINE;

/**
 * Reports package cohesion analysis results and writes detail files.
 */
public final class PlainEnglishPackageCohesionAnalysisResultReporter implements PackageCohesionAnalysisResultReporter {

    @Override
    public Class<CohesionAnalysisResult> resultType() {
        return CohesionAnalysisResult.class;
    }

    @Override
    public String report(CohesionAnalysisResult result) {
        String fileNames = PACKAGE_COHESIONS_FILE_NAME + ", " +
                PACKAGE_STRUCTURE_HINTS1_FILE_NAME + ", " +
                PACKAGE_STRUCTURE_HINTS2_FILE_NAME;

        ExistingPackageCohesionsFileWriter.writeToFile(result);
        PackageStructureHints1FileWriter.writeToFile(result.groupingResult());
        PackageStructureHints2FileWriter.writeToFile(result.subgraphDecomposition());

        var cohesiveGroupsSuffix = result.cohesiveNodeGroups()
                .map(groups -> {
                    CohesiveGroupsFileWriter.writeToFile(groups);
                    return ", " + COHESIVE_GROUPS_FILE_NAME;
                })
                .orElse("");
        fileNames += cohesiveGroupsSuffix;

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
