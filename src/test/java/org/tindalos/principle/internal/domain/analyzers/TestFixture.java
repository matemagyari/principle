package org.tindalos.principle.internal.domain.analyzers;

import org.tindalos.principle.internal.utils.logging.SimpleLogger;
import org.tindalos.principle.internal.utils.logging.TheLogger;

public final class TestFixture {

    private TestFixture() {
    }

    public static void setLogger() {
        TheLogger.setLogger(new SimpleLogger() {
            @Override
            public void info(String msg) {
                System.out.println(msg);
            }

            @Override
            public void error(String msg) {
                System.err.println(msg);
            }
        });
    }
}
