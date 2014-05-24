package org.tindalos.principle.infrastructure.service.jdepend

import org.tindalos.principle.domain.core.Metrics
import jdepend.framework.JavaPackage

class MetricsCalculator {

  def calculate(jPackage: JavaPackage) =
    new Metrics(jPackage.afferentCoupling(), jPackage.efferentCoupling(), jPackage.abstractness(), jPackage.instability(), jPackage.distance())
}