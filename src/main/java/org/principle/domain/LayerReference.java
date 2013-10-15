package org.principle.domain;

public class LayerReference {
    
    private String referer;
    private String referee;
    
    public LayerReference(String referer, String referee) {
        this.referer = referer;
        this.referee = referee;
    }

    @Override
    public String toString() {
        return referer + " -> " + referee;
    }
}