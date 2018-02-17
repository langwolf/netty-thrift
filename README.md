# Netty-thrift
Thrift on Netty
* base [nettythrift](https://github.com/houkx/nettythrift) and [nifty](https://github.com/facebook/nifty)
* add Multi-service mode
# Example
```Java
Map<String, TBaseProcessor<?>> map = Maps.newHashMap();
map.put("test", new TCalculator.Processor<TCalculator.Iface>(new CalcIfaceImpl()));	
map.put("test2", new TCalculator.Processor<TCalculator.Iface>(new CalcIfaceImpl()));
TNettyThriftServer.Args nettyArg = new TNettyThriftServer.Args(8090);
nettyArg.setMaxReadBuffer(1024 * 100);
nettyArg.setMap(map);
final TServer nettyServer = new TNettyThriftServer(nettyArg);
  new Thread(new Runnable() {
    @Override
    public void run() {
      nettyServer.serve();
    }
  }).start();
    
```
OR
```Java
Map<String, TBaseProcessor<?>> map = Maps.newHashMap();
map.put("test", new TCalculator.Processor<TCalculator.Iface>(new CalcIfaceImpl()));	
map.put("test2", new TCalculator.Processor<TCalculator.Iface>(new CalcIfaceImpl()));
ThriftServerDef[] serverDefs = ThriftServerDef.newBuilder().listen(port)
  .withProcessors(map)
  .using(Executors.newCachedThreadPool())
  .builds();
final MultiServerBootstrap server = new MultiServerBootstrap(serverDefs, 8089, TimeUnit.SECONDS.toMillis(15));
server.start();	
```
