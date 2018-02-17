/**
 * 
 */
package com.lioncorp.nettythrift.core;

import com.lioncorp.nettythrift.codec.HttpThriftBufDecoder;

import io.netty.channel.ChannelHandler;


public class DefaultHttpHandlerFactory implements HttpHandlerFactory {

	/* (non-Javadoc)
	 * @see io.netty5thrift.core.HttpHandlerFactory#create(io.netty5thrift.core.ThriftServerDef)
	 */
	@Override
	public ChannelHandler create(ThriftServerDef serverDef) {
		return new HttpThriftBufDecoder(serverDef);
	}

}
