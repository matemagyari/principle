package org.tindalos.principle.internal.infrastructure.reporters;

import java.io.File;

/**
 * Manages the reports output directory used by all analysis reporters.
 */
public class ReportsDirectoryManager {

    public static final String reportDirectoryPath = "./principle_reports";

    /**
     * Ensures the reports directory exists, creating it if necessary.
     *
     * @return the path to the reports directory
     */
    public static String ensureReportsDirectoryExists() {
        File reportDirectory = new File(reportDirectoryPath);
        if (!reportDirectory.exists()) {
            reportDirectory.mkdirs();
        }
        return reportDirectoryPath;
    }
}
