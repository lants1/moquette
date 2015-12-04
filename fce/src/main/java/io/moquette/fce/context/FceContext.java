package io.moquette.fce.context;

import io.moquette.fce.exception.FceSystemException;
import io.moquette.fce.model.common.ManagedScope;
import io.moquette.fce.model.common.ManagedZone;
import io.moquette.plugin.IBrokerConfig;
import io.moquette.plugin.IBrokerOperator;

public class FceContext extends BrokerContext{

	private static final String PROP_JKS_PATH = "plugin_jks_path";
	private static final String PROP_KEY_MANAGER_PASSWORD = "plugin_key_manager_password";
	private static final String PROP_KEY_STORE_PASSWORD = "plugin_key_store_password";

	private QuotaStore quotaDbServiceGlobal;
	private QuotaStore quotaDbServicePrivate;
	private ConfigurationStore configDbServiceGlobal;
	private ConfigurationStore configDbServicePrivate;
	private HashAssignmentStore hashAssignment;
	private SchemaAssignmentStore schemaAssignment;
	
	private String pluginUser;
	private String pluginPw;
	private String pluginIdentifier;

	public FceContext(IBrokerConfig config, IBrokerOperator brokerOperator) {
		super(config, brokerOperator);
		
		if(config.getProperty(PROP_JKS_PATH) == null || config.getProperty(PROP_KEY_MANAGER_PASSWORD) == null || config.getProperty(PROP_KEY_STORE_PASSWORD) == null){
			throw new FceSystemException("Missing properties for Plugin startup at least ssl_port or secure_websocket_port should be configured in the .conf file");
		}
	}

	public QuotaStore getQuotaStore(ManagedZone zone) {
		if (ManagedZone.QUOTA_GLOBAL.equals(zone)) {
			if (quotaDbServiceGlobal == null) {
				quotaDbServiceGlobal = new QuotaStore(getBrokerOperator(), zone);
			}
			return quotaDbServiceGlobal;
		} else if (ManagedZone.QUOTA_PRIVATE.equals(zone)) {
			if (quotaDbServicePrivate == null) {
				quotaDbServicePrivate = new QuotaStore(getBrokerOperator(), zone);
			}
			return quotaDbServicePrivate;
		}
		throw new FceSystemException("invalid quota db");
	}

	public ConfigurationStore getConfigurationStore(ManagedZone zone) {
		if (ManagedZone.CONFIG_GLOBAL.equals(zone)) {
			if (configDbServiceGlobal == null) {
				configDbServiceGlobal = new ConfigurationStore(getBrokerOperator(), zone);
			}
			return configDbServiceGlobal;
		} else if (ManagedZone.CONFIG_PRIVATE.equals(zone)) {
			if (configDbServicePrivate == null) {
				configDbServicePrivate = new ConfigurationStore(getBrokerOperator(), zone);
			}
			return configDbServicePrivate;
		}
		throw new FceSystemException("invalid config db");
	}

	public ConfigurationStore getConfigurationStore(ManagedScope scope) {
		if (ManagedScope.GLOBAL.equals(scope)) {
			return getConfigurationStore(ManagedZone.CONFIG_GLOBAL);
		} else if (ManagedScope.PRIVATE.equals(scope)) {
			return getConfigurationStore(ManagedZone.CONFIG_PRIVATE);
		}
		throw new FceSystemException("invalid scope for config db");
	}

	public QuotaStore getQuotaStore(ManagedScope scope) {
		if (ManagedScope.GLOBAL.equals(scope)) {
			return getQuotaStore(ManagedZone.QUOTA_GLOBAL);
		} else if (ManagedScope.PRIVATE.equals(scope)) {
			return getQuotaStore(ManagedZone.QUOTA_PRIVATE);
		}
		throw new FceSystemException("invalid scope for quota db");
	}

	public HashAssignmentStore getHashAssignment() {
		if (hashAssignment == null) {
			hashAssignment = new HashAssignmentStore();
		}
		return hashAssignment;
	}
	
	public SchemaAssignmentStore getSchemaAssignment() {
		if (schemaAssignment == null) {
			schemaAssignment = new SchemaAssignmentStore();
		}
		return schemaAssignment;
	}

	public String getPluginUser() {
		return pluginUser;
	}

	public void setPluginUser(String pluginUser) {
		this.pluginUser = pluginUser;
	}

	public String getPluginPw() {
		return pluginPw;
	}

	public void setPluginPw(String pluginPw) {
		this.pluginPw = pluginPw;
	}

	public String getPluginIdentifier() {
		return pluginIdentifier;
	}

	public void setPluginIdentifier(String pluginIdentifier) {
		this.pluginIdentifier = pluginIdentifier;
	}
	
	public String getKeyManagerPassword(){
		return getConfig().getProperty(PROP_KEY_MANAGER_PASSWORD,"");
	}
	
	public String getKeyStorePassword(){
		return getConfig().getProperty(PROP_KEY_STORE_PASSWORD,"");
	}
	
	public String getJksPath(){
		return getConfig().getProperty(PROP_JKS_PATH,"");
	}
	
	public boolean isInitialized() {
		return (getConfigurationStore(ManagedZone.CONFIG_GLOBAL).isInitialized()
				&& getQuotaStore(ManagedZone.QUOTA_GLOBAL).isInitialized()
				&& getConfigurationStore(ManagedZone.CONFIG_PRIVATE).isInitialized()
				&& getQuotaStore(ManagedZone.QUOTA_PRIVATE).isInitialized());
	}
}