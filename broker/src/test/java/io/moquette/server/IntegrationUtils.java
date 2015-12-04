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
package io.moquette.server;

import static io.moquette.commons.Constants.DEFAULT_MOQUETTE_STORE_MAP_DB_FILENAME;
import static io.moquette.commons.Constants.PERSISTENT_STORE_PROPERTY_NAME;

import java.io.File;
import java.util.Properties;

/**
 * Used to carry integration configurations.
 *
 * Created by andrea on 4/7/15.
 */
class IntegrationUtils {
    static String localMapDBPath() {
        String currentDir = System.getProperty("user.dir");
        return currentDir + File.separator + DEFAULT_MOQUETTE_STORE_MAP_DB_FILENAME;
    }

    public static Properties prepareTestPropeties() {
        Properties testProperties = new Properties();
        testProperties.put(PERSISTENT_STORE_PROPERTY_NAME, IntegrationUtils.localMapDBPath());
        return testProperties;
    }
}
