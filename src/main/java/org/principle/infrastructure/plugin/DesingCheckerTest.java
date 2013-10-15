package org.principle.infrastructure.plugin;

import org.junit.Test;
import org.principle.domain.checker.DesignCheckResults;
import org.principle.domain.checker.DesignChecker;
import org.principle.domain.core.DesingCheckerParameters;

public class DesingCheckerTest {

    private static final String BASE_PACKAGE = "org.principle.test";

    @Test
    public void designChecker() {

        DesingCheckerParameters parameters = new DesingCheckerParameters(BASE_PACKAGE, BASE_PACKAGE + ".b1",
                BASE_PACKAGE + ".b2", BASE_PACKAGE + ".b3");
        parameters.setLayers(BASE_PACKAGE + ".a", BASE_PACKAGE + ".b");
        DesignChecker designChecker = new DesignChecker(parameters);
        DesignCheckResults results = designChecker.execute(parameters);

        System.err.println(results.getErrorReport());
    }

    @Test
    public void checkItself() {
        String basePackage = "org.principle";
        DesingCheckerParameters parameters = new DesingCheckerParameters(basePackage + ".domain", basePackage
                + ".infrastructure");
        DesignChecker designChecker = new DesignChecker(parameters);
        DesignCheckResults results = designChecker.execute(parameters);

        System.err.println(results.getErrorReport());

    }

}
