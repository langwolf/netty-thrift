/**
 *
 */
package com.lioncorp.nettythrift.codec;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lioncorp.nettythrift.core.DefaultWriterListener;
import com.lioncorp.nettythrift.core.ThriftMessage;
import com.lioncorp.nettythrift.core.ThriftServerDef;
import com.lioncorp.nettythrift.transport.TNettyTransport;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ThriftMessageEncoder extends SimpleChannelInboundHandler<ThriftMessage> {
	private static Logger logger = LoggerFactory.getLogger(ThriftMessageEncoder.class);
	private final ThriftServerDef serverDef;

	public ThriftMessageEncoder(ThriftServerDef serverDef) {
		super(false);
		this.serverDef = serverDef;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ThriftMessage message)
			throws Exception {
		ByteBuf buffer = message.getContent();
		logger.debug("msg.content:: {}", buffer);
		try {
			TNettyTransport transport = new TNettyTransport(ctx.channel(), buffer);
			TProtocolFactory protocolFactory = message.getProtocolFactory();
			TProtocol protocol = protocolFactory.getProtocol(transport);
			serverDef.getNettyProcessor().process(ctx, protocol, protocol,
					new DefaultWriterListener(message, transport, ctx, serverDef));
		} catch (Throwable ex) {
			int refCount = buffer.refCnt();
			if (refCount > 0) {
				buffer.release(refCount);
			}
			throw ex;
		}
		
	}

}
