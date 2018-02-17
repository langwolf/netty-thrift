package com.lioncorp.nettythrift.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lioncorp.nettythrift.core.ThriftServerDef;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;

/**
 * 通用Thrift服务
 * 
 * @author HouKangxi
 *
 */
public class ServerBootstrap extends CommonServer implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(ServerBootstrap.class);

	private final ChannelGroup allChannels;
	private final ThriftServerDef serverDef;

	public ServerBootstrap(ThriftServerDef serverDef) {
		this(serverDef, new DefaultChannelGroup(new DefaultEventLoop()));
	}

	public ServerBootstrap(ThriftServerDef serverDef, ChannelGroup allChannels) {
		this.serverDef = serverDef;
		this.allChannels = allChannels;
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
		super.start(serverDef.serverPort, serverDef.codecInstaller);
	}

	public void start(int bossThreads, int workThreads) throws Exception {
		start(serverDef.serverPort, serverDef.codecInstaller, bossThreads, workThreads);
	}

}
