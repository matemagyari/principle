package org.tindalos.principle.domain.core

import org.tindalos.principle.domain.expectations.Checks

case class AnalysisPlan(expectations: Checks, basePackage: String)