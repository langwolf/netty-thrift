/**
 * 
 */
package com.lioncorp.nettythrift.protocol;


public interface ProtocolFactorySelectorFactory {

	ProtocolFactorySelector createProtocolFactorySelector(Class<?> ifaceClass);
}
