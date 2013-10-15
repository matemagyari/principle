package org.principle.domain.core;

import java.util.List;

import com.google.common.collect.Lists;

public class DesingCheckerParameters {
    
    private String basePackage;
    private String appPackage;
    private String domainPackage;
    private String infrastructurePackage;
    
    private List<String> layers;

    public DesingCheckerParameters(String basePackage, String appPackage, String domainPackage,
            String infrastructurePackage) {
        this.basePackage = basePackage;
        this.appPackage = appPackage;
        this.domainPackage = domainPackage;
        this.infrastructurePackage = infrastructurePackage;
    }
    public DesingCheckerParameters(String basePackage, String... layers) {
        this.basePackage = basePackage;
        setLayers(layers);
    }

    public String getBasePackage() {
        return basePackage;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public String getDomainPackage() {
        return domainPackage;
    }

    public String getInfrastructurePackage() {
        return infrastructurePackage;
    }

    public List<String> getLayers() {
        return layers;
    }

    public void setLayers(List<String> layers) {
        this.layers = layers;
    }
    public void setLayers(String... layers) {
        setLayers(Lists.newArrayList(layers));
    }

    public List<String> getOuterLayers(String layer) {
        int indexOf = layers.indexOf(layer);
        return layers.subList(0, indexOf);
    }
    
    

}
