/**
 * 
 */
package com.lioncorp.nettythrift.core;


public interface LogicExecutionStatistics {
	boolean shouldExecuteInIOThread(String method);

	void saveExecutionMillTime(String method, int exeTime);
}
