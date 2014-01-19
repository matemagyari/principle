package org.tindalos.principle.domain.core

import org.tindalos.principle.domain.expectations.DesignQualityExpectations

case class DesignQualityCheckConfiguration(expectations: DesignQualityExpectations, basePackage: String) {

  def getLayeringExpectations() = expectations.layering
}