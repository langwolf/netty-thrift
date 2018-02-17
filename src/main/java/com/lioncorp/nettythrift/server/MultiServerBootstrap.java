package com.lioncorp.nettythrift.server;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lioncorp.nettythrift.core.DefaultMultiChannelInitializer;
import com.lioncorp.nettythrift.core.ThriftServerDef;

import io.netty.channel.Channel;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;

/**
 * @author bjssgong
 *
 */
public class MultiServerBootstrap extends CommonServer implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(MultiServerBootstrap.class);

	private int serverPort = 8090; 
	private long clientIdleTimeout = TimeUnit.SECONDS.toMillis(15);
	private final ChannelGroup allChannels;
	private final ThriftServerDef[] serverDefs;
	

	public MultiServerBootstrap(ThriftServerDef[] serverDefs) {
		this(serverDefs, new DefaultChannelGroup(new DefaultEventLoop()));
	}
	
	public MultiServerBootstrap(ThriftServerDef[] serverDefs, ChannelGroup allChannels) {
		this.serverDefs = serverDefs;
		this.allChannels = allChannels;
		Runtime.getRuntime().addShutdownHook(new Thread(this));
	}
	
	public MultiServerBootstrap(ThriftServerDef[] serverDefs, int port, long clientIdleTimeout,
			ChannelGroup allChannels) {
		this.serverDefs = serverDefs;
		this.allChannels = allChannels;
		this.serverPort = port;
		this.clientIdleTimeout = clientIdleTimeout;
		Runtime.getRuntime().addShutdownHook(new Thread(this));
	}
	
	public ChannelGroup getAllChannels() {
		return allChannels;
	}

	@Override
	public void run() {
		logger.info("server closing[channels:{}]...", allChannels.size());
		close();
	}

	@Override
	public void close() {
		allChannels.close().awaitUninterruptibly();
		super.close();
	}

	public void start() throws Exception {
		super.start(serverPort, new DefaultMultiChannelInitializer<Channel>(serverDefs, clientIdleTimeout));
	}

	public void start(int bossThreads, int workThreads) throws Exception {
		start(serverPort, new DefaultMultiChannelInitializer<Channel>(serverDefs, clientIdleTimeout), bossThreads, workThreads);
	}

}
