package org.tindalos.guardrails.internal.utils.logging;

/**
 * Simple logging interface for architecture analysis output.
 */
public interface SimpleLogger {

    void info(String msg);

    void error(String msg);

}

