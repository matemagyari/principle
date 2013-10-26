package org.tindalos.principle.domain.core;

public class Metrics {

    private final Integer afferentCoupling;
    private final Integer efferentCoupling;
    private final Float abstractness;
    private final Float instability;
    private final Float distance;
    
    public static Metrics undefined() {
        return new Metrics(0, 0, 0f, 0f, 0f) {
            public boolean isCalculated() {
                return false;
            }
        };
    }
    

    public Metrics(Integer afferentCoupling, Integer efferentCoupling, Float abstractness, Float instability,
            Float distance) {
        this.afferentCoupling = afferentCoupling;
        this.efferentCoupling = efferentCoupling;
        this.abstractness = abstractness;
        this.instability = instability;
        this.distance = distance;
    }

    public Integer getAfferentCoupling() {
        return afferentCoupling;
    }

    public Integer getEfferentCoupling() {
        return efferentCoupling;
    }

    public Float getAbstractness() {
        return abstractness;
    }

    public Float getInstability() {
        return instability;
    }

    public Float getDistance() {
        return distance;
    }
    
    public boolean isCalculated() {
        return true;
    }


	@Override
	public String toString() {
		return "Metrics [afferentCoupling=" + afferentCoupling + ", efferentCoupling=" + efferentCoupling + ", abstractness=" + abstractness + ", instability="
				+ instability + ", distance=" + distance + "]";
	}
    
    
    
}
