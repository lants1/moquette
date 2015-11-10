package org.eclipse.moquette.fce;

import org.eclipse.moquette.plugin.BrokerAuthorizationPlugin;
import org.eclipse.moquette.plugin.BrokerOperator;

public class FcePlugin implements BrokerAuthorizationPlugin {

	@Override
	public void load(BrokerOperator brokerOperator) {
		brokerOperator.subscribe("/MANAGED_INTENT/#");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAuthorized() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onEvent() {
		// TODO Auto-generated method stub
		
	}

}
