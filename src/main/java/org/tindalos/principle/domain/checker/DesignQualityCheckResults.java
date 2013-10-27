package org.tindalos.principle.domain.checker;

import java.util.List;
import java.util.Map;

import org.tindalos.principle.domain.coredetector.CheckResult;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DesignQualityCheckResults {

	private final Map<Class<? extends CheckResult>, CheckResult> checkResults;

	public DesignQualityCheckResults(List<CheckResult> checkResults) {
		this.checkResults = Maps.newHashMap();
		for (CheckResult checkResult : checkResults) {
			this.checkResults.put(checkResult.getClass(), checkResult);
		}
	}

	public boolean hasErrors() {
		for (CheckResult checkResult : checkResults.values()) {
			if (checkResult.violationsDetected()) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public <T extends CheckResult> Optional<T> getResult(Class<T> clazz) {
		T result = (T) checkResults.get(clazz);

		if (result != null) {
			return Optional.of(result);
		} else {
			return Optional.absent();
		}
	}
	
	public List<CheckResult> resultList() {
		return Lists.newArrayList(checkResults.values());
	}

}
