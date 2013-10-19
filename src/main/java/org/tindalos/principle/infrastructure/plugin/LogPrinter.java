package org.tindalos.principle.infrastructure.plugin;

import org.apache.maven.plugin.logging.Log;
import org.tindalos.principle.app.service.impl.Printer;

public class LogPrinter implements Printer {

	private final Log log;

	public LogPrinter(Log log) {
		this.log = log;
	}

	public void printInfo(String text) {
		this.log.info(text);
	}	
	
	public void printWarning(String text) {
		this.log.warn(text);
	}

}
