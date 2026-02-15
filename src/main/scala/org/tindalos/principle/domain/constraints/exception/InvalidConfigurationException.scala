package org.tindalos.principle.domain.constraints.exception

case class InvalidConfigurationException(msg: String) extends RuntimeException(msg)