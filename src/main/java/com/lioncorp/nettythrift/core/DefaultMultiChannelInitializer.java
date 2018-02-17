package com.lioncorp.nettythrift.core;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ArrayUtils;

import com.lioncorp.nettythrift.codec.AwsProxyProtocolDecoder;
import com.lioncorp.nettythrift.codec.MultiThriftMessageEncoder;
import com.lioncorp.nettythrift.codec.ThriftMessageDecoder;
import com.lioncorp.nettythrift.protocol.DefaultProtocolFactorySelectorFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;


/**
 * @author bjssgong
 *
 * @param <CHANNEL>
 */
public class DefaultMultiChannelInitializer<CHANNEL extends Channel> extends ChannelInitializer<CHANNEL> {

	private final ThriftServerDef[] serverDefs;
	private  long clientIdleTimeout;
	
	public DefaultMultiChannelInitializer(ThriftServerDef[] serverDefs) {
		this.serverDefs = serverDefs;
	}

	public DefaultMultiChannelInitializer(ThriftServerDef[] serverDefs, long clientIdleTimeout) {
		this.serverDefs = serverDefs;
		this.clientIdleTimeout = clientIdleTimeout;
	}
	
	@Override
	protected void initChannel(CHANNEL channel) throws Exception {
		ChannelPipeline cp = channel.pipeline();
		cp.addLast("ProxyHandler", new AwsProxyProtocolDecoder());
//		cp.addLast("HttpDispatcher", new HttpCodecDispatcher(serverDefs[0]));
		cp.addLast("ThriftMessageDecoder", new ThriftMessageDecoder(null, 
				new DefaultProtocolFactorySelectorFactory().createProtocolFactorySelector(null)));
		cp.addLast("ThriftMessageEncoder", new MultiThriftMessageEncoder(serverDefs));
		if(ArrayUtils.isEmpty(serverDefs))
			return;		
		if (clientIdleTimeout > 0l) {
			cp.addLast("IdleHandler", new IdleDisconnectHandler(clientIdleTimeout, TimeUnit.MILLISECONDS));
		}
	}

}
