/**
 * 
 */
package com.lioncorp.nettythrift.core;

import java.util.Map;

/**
 * Factory for create 'TrafficForecast'
 * 
 *
 */
public interface TrafficForecastFactory {
	TrafficForecast create(Map<String, Integer> inits);
}
