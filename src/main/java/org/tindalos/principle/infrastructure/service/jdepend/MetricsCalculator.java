package org.tindalos.principle.infrastructure.service.jdepend;

import jdepend.framework.JavaPackage;

import org.tindalos.principle.domain.core.Metrics;

public class MetricsCalculator {
    
    public Metrics calculate(JavaPackage jPackage) {
        return new Metrics(jPackage.afferentCoupling(), jPackage.efferentCoupling(), jPackage.abstractness(), jPackage.instability(), jPackage.distance());
    }

}
