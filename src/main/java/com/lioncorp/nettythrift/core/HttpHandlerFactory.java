/**
 * 
 */
package com.lioncorp.nettythrift.core;

import io.netty.channel.ChannelHandler;


public interface HttpHandlerFactory {

	ChannelHandler create(ThriftServerDef def);
}
