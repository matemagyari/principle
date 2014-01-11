package org.tindalos.principle.domain.core

case class DomainException(val msg: String)
  extends RuntimeException(msg) 