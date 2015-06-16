package org.tindalos.principle.domain.core

case class DomainException(msg: String) extends RuntimeException(msg)