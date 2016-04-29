package org.tindalos.principle.infrastructure

import org.junit.Test
import org.scalatest.{Matchers, FlatSpec}
import org.tindalos.principle.infrastructure.plugin.ChecksReader

class ChecksReaderTest
//    extends FlatSpec with Matchers
{

  //  "ChecksReader" should "read up configuration file" in {
  //    ChecksReader.readFromFile()
  //  }

  @Test
  def readerTest() {
    val r = ChecksReader.readFromFile(Some("principle.yml"))
    println(s"RESULT: $r")
  }

}
