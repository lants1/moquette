package io.moquette.plugin;
import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.InterceptConnectMessage;
import io.moquette.interception.messages.InterceptDisconnectMessage;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.interception.messages.InterceptSubscribeMessage;
import io.moquette.interception.messages.InterceptUnsubscribeMessage;
import io.moquette.plugin.IBrokerInterceptionPlugin;

public class PluginInterceptionHandlerAdapter implements InterceptHandler {

	IBrokerInterceptionPlugin plugin;
	
	public PluginInterceptionHandlerAdapter(IBrokerInterceptionPlugin p){
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
