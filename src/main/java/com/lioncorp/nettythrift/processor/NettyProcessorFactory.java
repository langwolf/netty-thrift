/**
 * 
 */
package com.lioncorp.nettythrift.processor;

import com.lioncorp.nettythrift.core.ThriftServerDef;

public interface NettyProcessorFactory {
	NettyProcessor create(ThriftServerDef serverDef);
}
