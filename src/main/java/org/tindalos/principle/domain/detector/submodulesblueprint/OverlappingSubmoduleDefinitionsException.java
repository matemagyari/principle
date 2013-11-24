package org.tindalos.principle.domain.detector.submodulesblueprint;

import java.util.Set;

import org.tindalos.principle.domain.detector.submodulesblueprint.SubmoduleDefinitions.Overlap;


@SuppressWarnings("serial")
public class OverlappingSubmoduleDefinitionsException extends InvalidBlueprintDefinitionException {

	public OverlappingSubmoduleDefinitionsException(Set<Overlap> overlaps) {
		super(toMessage(overlaps));
	}

	private static String toMessage(Set<Overlap> overlaps) {
		String msg = "Overlapping submodules: ";
		for (Overlap overlap : overlaps) {
			msg += "\n";
			for (SubmoduleId submoduleId : overlap.submoduleIds()) {
				msg += submoduleId + " and ";
			}
			msg = msg.substring(0, msg.length()-4);
		}
		return msg;
	}

}
