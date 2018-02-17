/**
 *
 */
package com.lioncorp.nettythrift.codec;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolDecorator;
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


/**
 * @author bjssgong
 *
 */
public class MultiThriftMessageEncoder extends SimpleChannelInboundHandler<ThriftMessage> {
	private static Logger logger = LoggerFactory.getLogger(MultiThriftMessageEncoder.class);
	private final ThriftServerDef[] serverDefs;

	public MultiThriftMessageEncoder(ThriftServerDef[] serverDefs) {
		super(false);
		this.serverDefs = serverDefs;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ThriftMessage message)
			throws Exception {
		ByteBuf buffer = message.getContent();
		logger.debug("msg.content:: {}", buffer);
		try {
			TNettyTransport transport = new TNettyTransport(ctx.channel(),
					buffer);
			TProtocolFactory protocolFactory = message.getProtocolFactory();
			TProtocol protocol = protocolFactory.getProtocol(transport);
			if(null == serverDefs || serverDefs.length == 0){
				logger.error("No Server");
				return;
			}
			final TMessage msg = protocol.readMessageBegin();
			if (msg.type != TMessageType.CALL
					&& msg.type != TMessageType.ONEWAY) {
				throw new TException("This should not have happened!?");
			}

			int index = msg.name.indexOf(TMultiplexedProtocol.SEPARATOR);
			if (index < 0) {
				 throw new TException("Service name not found in message name: " + msg.name + ".  Did you " +
		                    "forget to use a TMultiplexProtocol in your client?");
			}
			String serviceName = msg.name.substring(0, index);
			ThriftServerDef serverDef = null;
			for(ThriftServerDef tmp : serverDefs){
				if(tmp.serviceName.equals(serviceName)){
					serverDef = tmp;
				}
			}
			if(null == serverDef){
				logger.error("Server is Not Exists");
				return;
			}
			TMessage standardMessage = new TMessage(
					msg.name.substring(serviceName.length()+ TMultiplexedProtocol.SEPARATOR.length()),
					msg.type, 
					msg.seqid);
			TProtocol in = new StoredMessageProtocol(protocol, standardMessage);
			serverDef.nettyProcessor.process(ctx, in, protocol,
					new DefaultWriterListener(message, transport, ctx,
							serverDef));
		} catch (Throwable ex) {
			int refCount = buffer.refCnt();
			if (refCount > 0) {
				buffer.release(refCount);
			}
			throw ex;
		}
		
	}
	private static class StoredMessageProtocol extends TProtocolDecorator {
        TMessage messageBegin;
        public StoredMessageProtocol(TProtocol protocol, TMessage messageBegin) {
            super(protocol);
            this.messageBegin = messageBegin;
        }
        @Override
        public TMessage readMessageBegin() throws TException {
            return messageBegin;
        }
    }
	public static void main(String[] args) throws Exception {
		String serviceName = "test";
		System.out.println("test:testestest".substring(serviceName.length()+ TMultiplexedProtocol.SEPARATOR.length()));
	}
	


}
