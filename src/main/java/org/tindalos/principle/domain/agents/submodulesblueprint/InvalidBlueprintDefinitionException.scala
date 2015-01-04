package org.tindalos.principle.domain.agents.submodulesblueprint

import org.tindalos.principle.domain.expectations.exception.InvalidConfigurationException

class InvalidBlueprintDefinitionException(override val msg: String)
  extends InvalidConfigurationException(msg) 