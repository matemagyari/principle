package org.tindalos.principle.domain.core;

import java.util.List;

import org.codehaus.plexus.util.StringUtils;

import com.google.common.collect.Lists;

public class DesignQualityCheckParameters {
    
    private final String basePackage;
    private List<String> layers;
    private Float maxSAPDistance;

    public DesignQualityCheckParameters(String basePackage, String... layers) {
        this(basePackage, Lists.newArrayList(layers));
    }

    public DesignQualityCheckParameters(String basePackage, List<String> layers) {
        checkNotEmpy("basePackage", basePackage);
        this.basePackage = basePackage;
        setLayers(layers);
    }
    
    public String getBasePackage() {
        return basePackage;
    }

    public List<String> getLayers() {
        return layers;
    }

    private void setLayers(List<String> layers) {
        this.layers = Lists.newArrayList();
        for (String layer : layers) {
            this.layers.add(basePackage + "." + layer);
        }
    }

    public List<String> getOuterLayers(String layer) {
        int indexOf = layers.indexOf(layer);
        return layers.subList(0, indexOf);
    }
    
    private void checkNotEmpy(String name, String value) {
        if (StringUtils.isBlank(value)) {
            throw new RuntimeException(name + " is empty");
        }
    }

	public Float getMaxSAPDistance() {
		return maxSAPDistance;
	}

	public void setMaxSAPDistance(Float maxSAPDistance) {
		this.maxSAPDistance = maxSAPDistance;
	}



}
