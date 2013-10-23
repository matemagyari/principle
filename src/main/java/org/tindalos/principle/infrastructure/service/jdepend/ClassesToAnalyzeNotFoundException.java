package org.tindalos.principle.infrastructure.service.jdepend;

import java.io.IOException;

@SuppressWarnings("serial")
public class ClassesToAnalyzeNotFoundException extends RuntimeException {

    public ClassesToAnalyzeNotFoundException(IOException ex) {
        super("/target/classes not found! " + ex.getMessage());
    }

}
