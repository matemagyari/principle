package org.tindalos.principle.domain.core.checkerparameter;

//DesignQualityCheckParameters
public class Checks {
	
	private Layering layering;
	private PackageCoupling packageCoupling;
	
	public Layering getLayering() {
		return layering;
	}
	
	public void setLayering(Layering layering) {
		this.layering = layering;
	}
	
	public PackageCoupling getPackageCoupling() {
		return packageCoupling;
	}
	
	public void setPackageCoupling(PackageCoupling packageCoupling) {
		this.packageCoupling = packageCoupling;
	}


}
