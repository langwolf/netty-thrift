package com.lioncorp.nettythrift.transport;

import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lioncorp.nettythrift.core.DefaultMultiChannelInitializer;
import com.lioncorp.nettythrift.core.ThriftServerDef;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author bjssgong
 *
 */
public class TNettyThriftServerTransport extends TServerTransport {
	private static Logger logger = LoggerFactory.getLogger(TNettyThriftServerTransport.class);
    private int serverPort;

    private long clientTimeout;
    private ChannelFuture channelFuture;

    private ThriftServerDef[] serverDefs;
    private int bossThreads;
    private int workThreads;

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    public TNettyThriftServerTransport(int port) {
        this(port, 0);
    }

    public TNettyThriftServerTransport(int port, long timeout) {
        this.serverPort = port;
        this.clientTimeout = timeout;
    }
    
    public TNettyThriftServerTransport(int port, int timeout, int bossThreads, int workThreads) {
        this.serverPort = port;
        this.clientTimeout = timeout;
        this.bossThreads = bossThreads;
        this.workThreads = workThreads;
    }
    
    @Override
    public void listen() throws TTransportException {
        
        bossGroup = new NioEventLoopGroup(bossThreads);
		workerGroup = new NioEventLoopGroup(workThreads);
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
					 .channel(NioServerSocketChannel.class)
					 .option(ChannelOption.SO_BACKLOG, 
							 Integer.parseInt(System.getProperty("so.BACKLOG", "100")))
					 .option(ChannelOption.SO_REUSEADDR,
							Boolean.parseBoolean(System.getProperty("so.REUSEADDR", "true")))
					 .handler(new LoggingHandler(LogLevel.DEBUG))
					 .childHandler(new DefaultMultiChannelInitializer<Channel>(serverDefs));
			if (clientTimeout > 0) {
                bootstrap.childOption(ChannelOption.SO_TIMEOUT, new Long(clientTimeout).intValue());
            }
			channelFuture = bootstrap.bind(serverPort).sync();
			logger.info("Server started and listen on port:{}", serverPort);
			channelFuture.channel().closeFuture().sync();
		} catch (InterruptedException ie) {
            throw new TTransportException(ie);
        } finally {
            close();
        }

    }

    @Override
    public void close() {
        try {
            channelFuture.channel().close();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public ThriftServerDef[] getServerDefs() {
		return serverDefs;
	}

	public void setServerDefs(ThriftServerDef[] serverDefs) {
		this.serverDefs = serverDefs;
	}

	@Override
    protected TTransport acceptImpl() throws TTransportException {
        return null;
    }

}
