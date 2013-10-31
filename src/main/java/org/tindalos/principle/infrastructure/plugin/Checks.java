package org.tindalos.principle.infrastructure.plugin;

import org.tindalos.principle.domain.core.checkerparameter.DesingQualityCheckParameters;
import org.tindalos.principle.domain.core.checkerparameter.Layering;
import org.tindalos.principle.domain.core.checkerparameter.PackageCoupling;

public class Checks implements DesingQualityCheckParameters {
	
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
