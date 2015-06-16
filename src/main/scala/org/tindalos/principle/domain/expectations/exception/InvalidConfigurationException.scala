package org.tindalos.principle.domain.expectations.exception

case class InvalidConfigurationException(msg: String) extends RuntimeException(msg)