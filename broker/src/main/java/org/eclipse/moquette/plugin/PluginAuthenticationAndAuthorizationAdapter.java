package org.eclipse.moquette.plugin;

import org.eclipse.moquette.spi.impl.security.IAuthenticator;
import org.eclipse.moquette.spi.impl.security.IAuthorizator;

public class PluginAuthenticationAndAuthorizationAdapter implements IAuthenticator, IAuthorizator {

	AuthenticationAndAuthorizationPlugin plugin;
	
	public PluginAuthenticationAndAuthorizationAdapter(AuthenticationAndAuthorizationPlugin p){
		this.plugin = p;
	}

	@Override
	public boolean canWrite(String topic, String user, String client) {
		return plugin.canWrite(topic, user, client);
	}

	@Override
	public boolean canRead(String topic, String user, String client) {
		return plugin.canRead(topic, user, client);
	}

	@Override
	public boolean checkValid(String username, byte[] password) {
		return plugin.checkValid(username, password);
	}
	

}
