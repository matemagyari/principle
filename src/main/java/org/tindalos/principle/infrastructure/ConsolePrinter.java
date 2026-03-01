package org.tindalos.principle.infrastructure;

import org.tindalos.principle.domain.Printer;

public class ConsolePrinter implements Printer {

    @Override
    public void printInfo(String text) {
        System.out.println(text);
    }

    @Override
    public void printWarning(String text) {
        System.err.println(text);
    }
}

