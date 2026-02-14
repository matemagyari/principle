package org.tindalos.principle.domain.checker

import org.tindalos.principle.domain.core.logging.TheLogger
import org.tindalos.principle.domain.core.logging.SimpleLogger

object TestFixture {

  def setLogger() = {
    TheLogger.setLogger(new SimpleLogger() {
      override def info(msg: String) = {
        System.out.println(msg)
      }

      override def error(msg: String) = {
        System.err.println(msg)
      }
    })
  }

}