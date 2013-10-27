package org.tindalos.principle.domain.checkerparameter;

import java.util.List;

import com.google.common.collect.Lists;

public class Layering extends Thresholders {

	private List<String> layers;

	public List<String> getLayers() {
		return Lists.newArrayList(layers);
	}

	public void setLayers(List<String> layers) {
		this.layers = Lists.newArrayList(layers);
	}

	public void setLayers(String... layers) {
		this.layers = Lists.newArrayList(layers);
	}

}
