package org.tindalos.principle.domain.expectations

class SubmodulesBlueprint(var location:String, threshold:Int = 0)  extends Thresholder(threshold) {
  def this() = this(null,0)
}