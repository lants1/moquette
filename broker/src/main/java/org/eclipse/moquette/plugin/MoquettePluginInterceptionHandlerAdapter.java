package org.eclipse.moquette.plugin;

import org.eclipse.moquette.interception.InterceptHandler;
import org.eclipse.moquette.interception.messages.InterceptConnectMessage;
import org.eclipse.moquette.interception.messages.InterceptDisconnectMessage;
import org.eclipse.moquette.interception.messages.InterceptPublishMessage;
import org.eclipse.moquette.interception.messages.InterceptSubscribeMessage;
import org.eclipse.moquette.interception.messages.InterceptUnsubscribeMessage;

public class MoquettePluginInterceptionHandlerAdapter implements InterceptHandler {

	BrokerInterceptionPlugin plugin;
	
	public MoquettePluginInterceptionHandlerAdapter(BrokerInterceptionPlugin p){
		this.plugin = p;
	}
	
	@Override
	public void onConnect(InterceptConnectMessage msg) {
		// TODO Auto-generated method stub
		plugin.onConnect(null);
		
	}

	@Override
	public void onDisconnect(InterceptDisconnectMessage msg) {
		// TODO Auto-generated method stub
		plugin.onDisconnect(null);
	}

	@Override
	public void onPublish(InterceptPublishMessage msg) {
		// TODO Auto-generated method stub
		plugin.onPublish(null);
	}

	@Override
	public void onSubscribe(InterceptSubscribeMessage msg) {
		// TODO Auto-generated method stub
		plugin.onSubscribe(null);
	}

	@Override
	public void onUnsubscribe(InterceptUnsubscribeMessage msg) {
		// TODO Auto-generated method stub
		plugin.onUnsubscribe(null);
	}

}
