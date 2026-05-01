package org.tindalos.guardrails.internal.domain.analyzers;

import org.tindalos.guardrails.internal.utils.logging.SimpleLogger;
import org.tindalos.guardrails.internal.utils.logging.TheLogger;

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
