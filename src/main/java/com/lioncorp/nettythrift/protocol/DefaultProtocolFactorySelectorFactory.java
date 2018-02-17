/**
 * 
 */
package com.lioncorp.nettythrift.protocol;


public class DefaultProtocolFactorySelectorFactory implements ProtocolFactorySelectorFactory {


	@Override
	public ProtocolFactorySelector createProtocolFactorySelector(Class<?> interfaceClass) {
		return new ProtocolFactorySelector(interfaceClass);
	}

}
