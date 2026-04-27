package org.tindalos.principle.internal.utils.logging;

/**
 * Singleton logger instance for the application.
 * Provides static access to logging functionality throughout the codebase.
 */
public final class TheLogger {

    private static SimpleLogger logger;

    private TheLogger() {
        // Private constructor to prevent instantiation
    }

    public static void setLogger(SimpleLogger aLogger) {
        logger = aLogger;
    }

    public static void info(String msg) {
        logger.info(msg);
    }

    public static void error(String msg) {
        logger.error(msg);
    }

}

