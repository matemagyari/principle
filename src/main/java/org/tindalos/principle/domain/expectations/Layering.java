package org.tindalos.principle.domain.expectations;

import java.util.List;

import org.tindalos.principle.domain.expectations.exception.InvalidConfigurationException;

import com.google.common.collect.Lists;

public class Layering extends Thresholder {
	
	public Layering() {
		super(0);
	}

	private List<String> layers;

	public List<String> getLayers() {
		if (layers == null || layers.isEmpty()) {
			throw new InvalidConfigurationException("Invalid layering expectation! No layers specified!");
		}
		return Lists.newArrayList(layers);
	}

	public void setLayers(List<String> layers) {
		this.layers = Lists.newArrayList(layers);
	}

	public void setLayers(String... layers) {
		this.layers = Lists.newArrayList(layers);
	}

}
