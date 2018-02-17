/**
 *
 */
package com.lioncorp.nettythrift.core;

import java.util.concurrent.TimeUnit;

import com.lioncorp.nettythrift.codec.AwsProxyProtocolDecoder;
import com.lioncorp.nettythrift.codec.HttpCodecDispatcher;
import com.lioncorp.nettythrift.codec.ThriftMessageDecoder;
import com.lioncorp.nettythrift.codec.ThriftMessageEncoder;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

public class DefaultChannelInitializer<CHANNEL extends Channel> extends ChannelInitializer<CHANNEL> {

	private final ThriftServerDef serverDef;

	public DefaultChannelInitializer(ThriftServerDef serverDef) {
		this.serverDef = serverDef;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.netty5thrift.core.ContextHandlerInstaller#installHandlers(io.netty.
	 * channel.ChannelPipeline)
	 */
	@Override
	protected void initChannel(CHANNEL channel) throws Exception {
		ChannelPipeline cp = channel.pipeline();
		cp.addLast("ProxyHandler", new AwsProxyProtocolDecoder());
		cp.addLast("HttpDispatcher", new HttpCodecDispatcher(serverDef));
		cp.addLast("ThriftMessageDecoder", new ThriftMessageDecoder(serverDef, null));
		cp.addLast("ThriftMessageEncoder", new ThriftMessageEncoder(serverDef));
		long idles = serverDef.clientIdleTimeout;
		if (idles > 0) {
			cp.addLast("IdleHandler", new IdleDisconnectHandler(idles, TimeUnit.MILLISECONDS));
		}
	}

}
