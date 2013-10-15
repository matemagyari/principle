package org.principle.domain.core;

import java.util.List;

import org.codehaus.plexus.util.StringUtils;

import com.google.common.collect.Lists;

public class DesingCheckerParameters {
    
    private String basePackage;
    
    private List<String> layers;

    public DesingCheckerParameters(String basePackage, String... layers) {
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

    public void setLayers(List<String> layers) {
        this.layers = Lists.newArrayList();
        for (String layer : layers) {
            this.layers.add(basePackage + "." + layer);
        }
    }
    public void setLayers(String... layers) {
        setLayers(Lists.newArrayList(layers));
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

}
