package org.tindalos.principle.domain.checker

import org.tindalos.principle.domain.core.logging.TheLogger
import org.tindalos.principle.domain.core.logging.ScalaLogger

object TestFixture {

  def setLogger() = {
    TheLogger.setLogger(new ScalaLogger() {
      override def info(msg: String) = {
        System.out.println(msg)
      }

      override def error(msg: String) = {
        System.err.println(msg)
      }
    })
  }

}