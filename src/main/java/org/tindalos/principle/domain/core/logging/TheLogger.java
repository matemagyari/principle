package org.tindalos.principle.domain.core.logging;

public class TheLogger {
    
    private static ScalaLogger logger;
    
    public static void setLogger(ScalaLogger logger) {
        TheLogger.logger = logger;
    }
    
    public static void info(String msg) {
        logger.info(msg);
    }
    
    public static void error(String msg) {
        logger.error(msg);
    }

}
