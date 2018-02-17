/**
 * 
 */
package com.lioncorp.nettythrift.processor;

import org.apache.thrift.protocol.TProtocol;

import com.lioncorp.nettythrift.core.WriterHandler;

import io.netty.channel.ChannelHandlerContext;

public interface NettyProcessor {

	void process(ChannelHandlerContext ctx, TProtocol in, TProtocol out, WriterHandler onComplete)
			throws Exception;
}
