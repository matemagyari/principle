package org.tindalos.principle.domain.core.logging;

public class TheLogger {
    
    private static Logger logger;
    
    public static void setLogger(Logger logger) {
        TheLogger.logger = logger;
    }
    
    public static void info(String msg) {
        logger.info(msg);
    }
    
    public static void error(String msg) {
        logger.error(msg);
    }

}
