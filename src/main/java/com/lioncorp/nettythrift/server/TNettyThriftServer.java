package com.lioncorp.nettythrift.server;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lioncorp.nettythrift.core.ThriftServerDef;
import com.lioncorp.nettythrift.core.ThriftServerDefBuilderBase;
import com.lioncorp.nettythrift.transport.TNettyThriftServerTransport;

/**
 * @author bjssgong
 *
 */
public class TNettyThriftServer extends TServer {

    private static final Logger logger = LoggerFactory.getLogger(TNettyThriftServer.class);

    public static class Args extends AbstractServerArgs<Args> {

        private int port;
        private int maxReadBuffer;
        private long clientTimeout;
        private Map<String, TBaseProcessor<?>> map;

        public Args(int port) {
            super(new TNettyThriftServerTransport(port));
            this.clientTimeout = 0;
        }

        public Args(int port, long timeout) {
            super(new TNettyThriftServerTransport(port, timeout));
            this.clientTimeout = timeout;
        }

        public Map<String, TBaseProcessor<?>> getMap() {
			return map;
		}

		public void setMap(Map<String, TBaseProcessor<?>> map) {
			this.map = map;
		}

		public int getPort() {
            return port;
        }

        public int getMaxReadBuffer() {
            return maxReadBuffer;
        }

        public void setMaxReadBuffer(int maxReadBuffer) {
            this.maxReadBuffer = maxReadBuffer;
        }

        public long getClientTimeout() {
            return clientTimeout;
        }
    }

    private Args args;

//    private ThriftServerDef[] serverDefs;

//    private static final int DEFAULT_MAX_READ_LENGTH = Integer.MAX_VALUE;

    public TNettyThriftServer(Args args) {
        super(args);
        this.args = args;
    }

    @Override
    public void serve() {
        int maxReadSize = args.getMaxReadBuffer();
        if (maxReadSize <= 0) {
            maxReadSize = ThriftServerDefBuilderBase.MAX_FRAME_SIZE;
        }
        long clientTimeout = args.getClientTimeout();
        clientTimeout = (clientTimeout <= 0)? TimeUnit.SECONDS.toMillis(15) : clientTimeout;
		ThriftServerDef[] serverDefs = ThriftServerDef.newBuilder()
				.listen(args.getPort())
				.withProcessors(args.getMap())
				.limitFrameSizeTo(maxReadSize)
//				.using(Executors.newCachedThreadPool())
				.clientIdleTimeout(clientTimeout)
				.builds();

        TNettyThriftServerTransport serverTransport = (TNettyThriftServerTransport) serverTransport_;
        serverTransport.setServerDefs(serverDefs);
        try {
        	serverTransport.listen();
        } catch (TTransportException e) {
        	logger.error("Netty Server Start error:{}", e);
            return;
        }      
    }

    public Args getArgs() {
        return args;
    }

    public void setArgs(Args args) {
        this.args = args;
    }
}
