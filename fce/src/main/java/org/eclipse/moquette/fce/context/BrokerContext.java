package org.eclipse.moquette.fce.context;

import org.eclipse.moquette.fce.exception.FceSystemException;
import org.eclipse.moquette.plugin.IBrokerConfig;
import org.eclipse.moquette.plugin.IBrokerOperator;

public abstract class BrokerContext {

	private static final String PROP_SSL_PORT = "ssl_port";
	private static final String PROP_SECURE_WEBSOCKET = "secure_websocket_port";
	
	private final IBrokerConfig config;
	private final IBrokerOperator brokerOperator;
	
	public BrokerContext(IBrokerConfig config, IBrokerOperator brokerOperator) {
		this.config = config;
		this.brokerOperator = brokerOperator;
		
		if(config.getProperty(PROP_SSL_PORT) == null && config.getProperty(PROP_SECURE_WEBSOCKET) == null){
			throw new FceSystemException("Missing properties for Broker startup at least ssl_port or secure_websocket_port should be configured in the .conf file");
		}
	}
	
	public IBrokerConfig getConfig() {
		return config;
	}

	public IBrokerOperator getBrokerOperator() {
		return brokerOperator;
	}

	public String getSslPort(){
		return getConfig().getProperty(PROP_SSL_PORT, "");
	}
	
	public String getSecureWebsocketPort(){
		return getConfig().getProperty(PROP_SECURE_WEBSOCKET, "");
	}
}
