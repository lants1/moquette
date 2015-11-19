package org.eclipse.moquette.plugin;

import java.nio.ByteBuffer;

import org.eclipse.moquette.spi.impl.security.IAuthenticator;
import org.eclipse.moquette.spi.impl.security.IAuthorizator;

public class PluginAuthenticationAndAuthorizationAdapter implements IAuthenticator, IAuthorizator {

	AuthenticationAndAuthorizationPlugin plugin;
	
	public PluginAuthenticationAndAuthorizationAdapter(AuthenticationAndAuthorizationPlugin p){
		this.plugin = p;
	}

	@Override
	public boolean canWrite(String topic, String user, String clientId, Boolean anonymous, ByteBuffer msg) {
		return plugin.canWrite(new AuthorizationProperties(topic, user, clientId, anonymous, msg));
	}

	@Override
	public boolean canRead(String topic, String user, String clientId, Boolean anonymous, ByteBuffer msg) {
		return plugin.canRead(new AuthorizationProperties(topic, user, clientId, anonymous, msg));
	}

	@Override
	public boolean checkValid(String username, byte[] password, String clientId) {
		return plugin.checkValid(new AuthenticationProperties(username, password, clientId));
	}
	
}
