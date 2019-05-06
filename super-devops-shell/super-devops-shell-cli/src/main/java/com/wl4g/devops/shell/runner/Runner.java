package com.wl4g.devops.shell.runner;

public interface Runner {

	/**
	 * Running
	 * 
	 * @param args
	 */
	void run(String[] args);

	/**
	 * Shutdown
	 * 
	 * @param line
	 */
	void shutdown(String line);

}
