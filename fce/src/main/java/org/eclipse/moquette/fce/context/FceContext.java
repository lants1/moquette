package org.eclipse.moquette.fce.context;

import org.eclipse.moquette.fce.exception.FceSystemException;
import org.eclipse.moquette.fce.model.common.ManagedScope;
import org.eclipse.moquette.fce.model.common.ManagedZone;
import org.eclipse.moquette.plugin.IBrokerConfig;
import org.eclipse.moquette.plugin.IBrokerOperator;

public class FceContext {

	private static final String PROPS_PLUGIN_KEY_MANAGER_PASSWORD = "plugin_key_manager_password";
	private static final String PROPS_PLUGIN_KEY_STORE_PASSWORD = "plugin_key_store_password";
	private static final String PROPS_PLUGIN_JKS_PATH = "plugin_jks_path";
	
	private final IBrokerConfig pluginConfig;
	private final IBrokerOperator brokerOperator;
	
	private QuotaStore quotaDbServiceGlobal;
	private QuotaStore quotaDbServicePrivate;
	private ConfigurationStore configDbServiceGlobal;
	private ConfigurationStore configDbServicePrivate;
	private HashAssignmentStore hashAssignment;
	private SchemaAssignmentStore schemaAssignment;
	
	private String pluginUser;
	private String pluginPw;
	private String pluginIdentifier;

	public FceContext(IBrokerConfig pluginConfig, IBrokerOperator brokerOperator) {
		super();
		this.pluginConfig = pluginConfig;
		this.brokerOperator = brokerOperator;
	}

	public IBrokerConfig getPluginConfig() {
		return pluginConfig;
	}

	public IBrokerOperator getBrokerOperator() {
		return brokerOperator;
	}

	public QuotaStore getQuotaStore(ManagedZone zone) {
		if (ManagedZone.QUOTA_GLOBAL.equals(zone)) {
			if (quotaDbServiceGlobal == null) {
				quotaDbServiceGlobal = new QuotaStore(brokerOperator, zone);
			}
			return quotaDbServiceGlobal;
		} else if (ManagedZone.QUOTA_PRIVATE.equals(zone)) {
			if (quotaDbServicePrivate == null) {
				quotaDbServicePrivate = new QuotaStore(brokerOperator, zone);
			}
			return quotaDbServicePrivate;
		}
		throw new FceSystemException("invalid quota db");
	}

	public ConfigurationStore getConfigurationStore(ManagedZone zone) {
		if (ManagedZone.CONFIG_GLOBAL.equals(zone)) {
			if (configDbServiceGlobal == null) {
				configDbServiceGlobal = new ConfigurationStore(brokerOperator, zone);
			}
			return configDbServiceGlobal;
		} else if (ManagedZone.CONFIG_PRIVATE.equals(zone)) {
			if (configDbServicePrivate == null) {
				configDbServicePrivate = new ConfigurationStore(brokerOperator, zone);
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
	
	public String getPluginKeyManagerPassword(){
		return getPluginConfig().getProperty(FceContext.PROPS_PLUGIN_KEY_MANAGER_PASSWORD);
	}
	
	public String getPluginKeyStorePassword(){
		return getPluginConfig().getProperty(FceContext.PROPS_PLUGIN_KEY_STORE_PASSWORD);
	}
	
	public String getPluginJksPath(){
		return getPluginConfig().getProperty(FceContext.PROPS_PLUGIN_JKS_PATH);
	}
}