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
package io.moquette.spi.impl.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.moquette.spi.impl.subscriptions.SubscriptionsStore;

import static io.moquette.spi.impl.security.Authorization.Permission.READ;
import static io.moquette.spi.impl.security.Authorization.Permission.READWRITE;
import static io.moquette.spi.impl.security.Authorization.Permission.WRITE;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.*;

/**
 * Used by the ACLFileParser to push all authorizations it finds.
 * ACLAuthorizator uses it in read mode to check it topics matches the ACLs.
 *
 * Not thread safe.
 *
 * @author andrea
 */
class AuthorizationsCollector implements IAuthorizator {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationsCollector.class);

    private List<Authorization> m_globalAuthorizations = new ArrayList();
    private List<Authorization> m_patternAuthorizations = new ArrayList();
    private Map<String, List<Authorization>> m_userAuthorizations = new HashMap();
    private boolean m_parsingUsersSpecificSection = false;
    private boolean m_parsingPatternSpecificSection = false;
    private String m_currentUser = "";

    static final AuthorizationsCollector emptyImmutableCollector() {
        AuthorizationsCollector coll = new AuthorizationsCollector();
        coll.m_globalAuthorizations = Collections.emptyList();
        coll.m_patternAuthorizations = Collections.emptyList();
        coll.m_userAuthorizations = Collections.emptyMap();
        return coll;
    }

    void parse(String line) throws ParseException {
        Authorization acl = parseAuthLine(line);
        if (acl == null) {
            //skip it's a user
            return;
        }
        if (m_parsingUsersSpecificSection) {
            //TODO in java 8 switch to m_userAuthorizations.putIfAbsent(m_currentUser, new ArrayList());
            if (!m_userAuthorizations.containsKey(m_currentUser)) {
                m_userAuthorizations.put(m_currentUser, new ArrayList());
            }
            List<Authorization> userAuths = m_userAuthorizations.get(m_currentUser);
            userAuths.add(acl);
        } else if (m_parsingPatternSpecificSection) {
            m_patternAuthorizations.add(acl);
        } else {
            m_globalAuthorizations.add(acl);
        }
    }

    protected Authorization parseAuthLine(String line) throws ParseException {
        String[] tokens = line.split("\\s+");
        String keyword = tokens[0].toLowerCase();
        switch (keyword) {
            case "topic":
                return createAuthorization(line, tokens);
            case "user":
                m_parsingUsersSpecificSection = true;
                m_currentUser = tokens[1];
                m_parsingPatternSpecificSection = false;
                return null;
            case "pattern":
                m_parsingUsersSpecificSection = false;
                m_currentUser = "";
                m_parsingPatternSpecificSection = true;
                return createAuthorization(line, tokens);
            default:
                throw new ParseException(String.format("invalid line definition found %s", line), 1);
        }
    }

    private Authorization createAuthorization(String line, String[] tokens) throws ParseException {
        if (tokens.length > 2) {
            //if the tokenized lines has 3 token the second must be the permission
            try {
                Authorization.Permission permission = Authorization.Permission.valueOf(tokens[1].toUpperCase());
                //bring topic with all original spacing
                String topic = line.substring(line.indexOf(tokens[2]));

                return new Authorization(topic, permission);
            } catch (IllegalArgumentException iaex) {
                throw new ParseException("invalid permission token", 1);
            }
        }
        String topic = tokens[1];
        return new Authorization(topic);
    }

    @Override
    public boolean canWrite(String topic, String user, String client, Boolean anonymous, ByteBuffer message) {
        return canDoOperation(topic, WRITE, user, client);
    }

    @Override
    public boolean canRead(String topic, String user, String client, Boolean anonymous,  ByteBuffer message) {
        return canDoOperation(topic, READ, user, client);
    }

    private boolean canDoOperation(String topic, Authorization.Permission permission, String username, String client) {
        if (matchACL(m_globalAuthorizations, topic, permission))  {
            return true;
        }

        if (isNotEmpty(client) || isNotEmpty(username)) {
            for (Authorization auth : m_patternAuthorizations) {
                String substitutedTopic = auth.topic.replace("%c", client).replace("%u", username);
                if (auth.grant(permission)) {
                    if (SubscriptionsStore.matchTopics(topic, substitutedTopic)) {
                        return true;
                    }
                }
            }
        }

        if (isNotEmpty(username)) {
            if (m_userAuthorizations.containsKey(username)) {
                List<Authorization> auths = m_userAuthorizations.get(username);
                if (matchACL(auths, topic, permission))  {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matchACL(List<Authorization> auths, String topic, Authorization.Permission permission) {
        for (Authorization auth : auths) {
            if (auth.grant(permission)) {
                if (SubscriptionsStore.matchTopics(topic, auth.topic)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isNotEmpty(String client) {
        return client != null && !client.isEmpty();
    }

    public boolean isEmpty() {
        return m_globalAuthorizations.isEmpty();
    }
}
