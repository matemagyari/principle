package org.tindalos.principle.infrastructure.plugin.parameter;

import java.util.List;

import com.google.common.collect.Lists;

public class Layering extends Thresholders {
	
	private List<String> layers;
	
	public List<String> getLayers() {
		return Lists.newArrayList(layers);
	}

}
