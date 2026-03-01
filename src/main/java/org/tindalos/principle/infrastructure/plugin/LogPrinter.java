package org.tindalos.principle.infrastructure.plugin;

import org.apache.maven.plugin.logging.Log;
import org.tindalos.principle.app.Printer;

public class LogPrinter implements Printer {

    private final Log log;

    public LogPrinter(Log log) {
        this.log = log;
    }

    @Override
    public void printInfo(String text) {
        log.info(text);
    }

    @Override
    public void printWarning(String text) {
        log.warn(text);
    }
}

