/*
 * Copyright (c) 2012-2015 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package org.eclipse.moquette.server;

import org.eclipse.moquette.plugin.IAuthenticationAndAuthorizationPlugin;
import org.eclipse.moquette.plugin.IBrokerInterceptionPlugin;
import org.eclipse.moquette.plugin.IBrokerPlugin;
import org.eclipse.moquette.plugin.MoquetteOperator;
import org.eclipse.moquette.plugin.PluginAuthenticationAndAuthorizationAdapter;
import org.eclipse.moquette.plugin.PluginInterceptionHandlerAdapter;
import org.eclipse.moquette.server.config.FilesystemConfig;
import org.eclipse.moquette.server.config.IConfig;
import org.eclipse.moquette.server.config.MemoryConfig;
import org.eclipse.moquette.server.netty.NettyAcceptor;
import org.eclipse.moquette.spi.impl.ProtocolProcessor;
import org.eclipse.moquette.spi.impl.SimpleMessaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;

import static org.eclipse.moquette.commons.Constants.PERSISTENT_STORE_PROPERTY_NAME;

/**
 * Launch a configured version of the server.
 * 
 * @author andrea
 */
public class Server {

	private static final Logger LOG = LoggerFactory.getLogger(Server.class);

	private ServerAcceptor m_acceptor;

	private List<IBrokerPlugin> loadedPlugins = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		final Server server = new Server();
		server.startServer();

		// Bind a shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				server.stopServer();
			}
		});
	}

	/**
	 * Starts Moquette bringing the configuration from the file located at
	 * m_config/moquette.conf
	 */
	public void startServer() throws IOException {
		final IConfig config = new FilesystemConfig();
		startServer(config);
	}

	/**
	 * Starts Moquette bringing the configuration from the given file
	 */
	public void startServer(File configFile) throws IOException {
		LOG.info("Using m_config file: " + configFile.getAbsolutePath());
		final IConfig config = new FilesystemConfig(configFile);
		startServer(config);
	}

	/**
	 * Starts the server with the given properties.
	 * 
	 * Its suggested to at least have the following properties:
	 * <ul>
	 * <li>port</li>
	 * <li>password_file</li>
	 * </ul>
	 */
	public void startServer(Properties configProps) throws IOException {
		final IConfig config = new MemoryConfig(configProps);

		startServer(config);
	}

	/**
	 * Starts Moquette bringing the configuration files from the given Config
	 * implementation.
	 */
	public void startServer(IConfig config) throws IOException {
		LOG.info("Server starting...");
		final String handlerProp = System.getProperty("intercept.handler");
		if (handlerProp != null) {
			config.setProperty("intercept.handler", handlerProp);
		}
		LOG.info("Persistent store file: " + config.getProperty(PERSISTENT_STORE_PROPERTY_NAME));
		final ProtocolProcessor processor = SimpleMessaging.getInstance().init(config);

		m_acceptor = new NettyAcceptor();
		m_acceptor.initialize(processor, config);
		
		loadPlugins(config, processor);

		LOG.info("Server started.");
	}

	public void stopServer() {
		LOG.info("Server stopping...");
		unloadPlugins();
		m_acceptor.close();
		SimpleMessaging.getInstance().shutdown();
		LOG.info("Server stopped");
	}

	private void loadPlugins(IConfig config, final ProtocolProcessor processor) {
		LOG.info("Try to load plugins...");
		ServiceLoader<IBrokerPlugin> loader = ServiceLoader.load(IBrokerPlugin.class);

		boolean alreadyLoadedAuthenticationAndAuthorizationPlugin = false;

		for (IBrokerPlugin p : loader) {
			if (p instanceof IAuthenticationAndAuthorizationPlugin) {
				// Only one Authentication and Authorization Plugin allowed.
				if (!alreadyLoadedAuthenticationAndAuthorizationPlugin) {
					IAuthenticationAndAuthorizationPlugin currentPlugin = (IAuthenticationAndAuthorizationPlugin) p;
					currentPlugin.load(config, new MoquetteOperator(processor));
					PluginAuthenticationAndAuthorizationAdapter pluginAdapter = new PluginAuthenticationAndAuthorizationAdapter(
							currentPlugin);
					// plugin replaces already defined Authenticator and Authorizator
					processor.setAuthenticator(pluginAdapter);
					processor.setAuthorizator(pluginAdapter);
					loadedPlugins.add(currentPlugin);
					LOG.info("Loaded AuthenticationAndAuthorizationPlugin: " + currentPlugin.getPluginIdentifier());
				}
				alreadyLoadedAuthenticationAndAuthorizationPlugin = true;
			}
			if (p instanceof IBrokerInterceptionPlugin) {
				IBrokerInterceptionPlugin currentPlugin = (IBrokerInterceptionPlugin) p;
				currentPlugin.load(config, new MoquetteOperator(processor));
				processor.addInterceptionHandler(new PluginInterceptionHandlerAdapter(currentPlugin));
				loadedPlugins.add(currentPlugin);
				LOG.info("Loaded BrokerInterceptionPlugin: " + currentPlugin.getPluginIdentifier());
			}
		}
	}

	private void unloadPlugins() {
		for (IBrokerPlugin plugin : loadedPlugins) {
			plugin.unload();
			LOG.info("Unloaded Broker Plugin: " + plugin.getPluginIdentifier());
		}
	}
}
