package org.tindalos.principle.domain.expectations.exception

case class InvalidConfigurationException(val msg: String)
  extends RuntimeException(msg) 