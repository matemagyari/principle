package org.tindalos.principle.domain.checker;

import org.tindalos.principle.domain.core.logging.Logger;
import org.tindalos.principle.domain.core.logging.TheLogger;

public class TestFixture {
    
    public static void setLogger() {
        TheLogger.setLogger(new Logger() {
            public void info(String msg) {
                System.out.println(msg);
            }

            public void error(String msg) {
                System.err.println(msg);
            }
        });
    }

}
