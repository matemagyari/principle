package org.principle.infrastructure.plugin;

import org.junit.Test;
import org.principle.domain.checker.DesignCheckResults;
import org.principle.domain.checker.DesignChecker;
import org.principle.domain.core.DesingCheckerParameters;

public class DesingCheckerTest {

    @Test
    public void checkItself() {
        String basePackage = "org.principle";
        DesingCheckerParameters parameters = new DesingCheckerParameters(basePackage, "infrastructure", "domain");
        DesignChecker designChecker = new DesignChecker(parameters);
        DesignCheckResults results = designChecker.execute();

        System.err.println(results.getErrorReport());

    }

}
