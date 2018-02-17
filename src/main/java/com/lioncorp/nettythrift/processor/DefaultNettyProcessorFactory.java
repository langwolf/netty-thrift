/**
 * 
 */
package com.lioncorp.nettythrift.processor;

import com.lioncorp.nettythrift.core.ThriftServerDef;

public class DefaultNettyProcessorFactory implements NettyProcessorFactory {

	/* (non-Javadoc)
	 * @see io.netty5thrift.core.NettyProcessorFactory#create(io.netty5thrift.core.ThriftServerDef)
	 */
	@Override
	public NettyProcessor create(ThriftServerDef serverDef) {
		return new DefaultNettyProcessor(serverDef);
	}

}
