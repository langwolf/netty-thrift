package com.lioncorp.nettythrift;

import java.util.Map;

import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;

import com.google.common.collect.Maps;
import com.lioncorp.nettythrift.server.TNettyThriftServer;

public class TestServer {

	public static void main(String[] args) throws Exception {
		Map<String, TBaseProcessor<?>> map = Maps.newHashMap();
		map.put("test", new TCalculator.Processor<TCalculator.Iface>(new CalcIfaceImpl()));
		map.put("test2", new TCalculator.Processor<TCalculator.Iface>(new CalcIfaceImpl()));
		com.lioncorp.nettythrift.server.TNettyThriftServer.Args nettyArg = new TNettyThriftServer.Args(8090);
//		nettyArg.setMaxReadBuffer(1024 * 100);
		nettyArg.setMap(map);
		final TServer nettyServer = new TNettyThriftServer(nettyArg);

		new Thread(()->{
			nettyServer.serve();
		}).start();
	}

	
	private static class CalcIfaceImpl implements TCalculator.Iface {

		@Override
		public String ping() throws TException {
			System.out.println("***ping()...");
			return "PONG";
		}

		@Override
		public int add(int num1, int num2) throws TException {
			System.out.printf("***add:(%d, %d)\n", num1, num2);
			return num1 + num2;
		}

		@Override
		public void zip(String str, int type) throws TException {
			System.out.printf("***zip:(%s, %d)\n", str, type);
		}

		@Override
		public void uploadAction(String str) throws TException {
			System.out.printf("***uploadAction:(%s)\n", str);
		}
	}
}
