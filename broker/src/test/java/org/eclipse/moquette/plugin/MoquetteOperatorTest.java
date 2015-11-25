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
package org.eclipse.moquette.plugin;

import java.nio.ByteBuffer;
import java.util.*;

import org.eclipse.moquette.interception.InterceptHandler;
import org.eclipse.moquette.proto.messages.*;
import org.eclipse.moquette.spi.IMessagesStore;
import org.eclipse.moquette.spi.ISessionsStore;
import org.eclipse.moquette.spi.impl.DummyChannel;
import org.eclipse.moquette.spi.impl.MemoryStorageService;
import org.eclipse.moquette.spi.impl.MockAuthenticator;
import org.eclipse.moquette.spi.impl.ProtocolProcessor;
import org.eclipse.moquette.spi.impl.security.PermitAllAuthorizator;
import org.eclipse.moquette.spi.impl.subscriptions.SubscriptionsStore;

import org.eclipse.moquette.proto.messages.AbstractMessage.QOSType;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class MoquetteOperatorTest {
    final static String FAKE_TOPIC = "/news";
    final static String FAKE_SUBTOPIC = "/news/subtopic";
    final static String FAKE_TOPICANDSUBTOPICFILTER = "/#";
    
    final static String TEST_USER = "fakeuser";
    final static byte[] TEST_PWD = "fakepwd".getBytes();


    final static List<InterceptHandler> EMPTY_OBSERVERS = Collections.emptyList();
  //  final static BrokerInterceptor NO_OBSERVERS_INTERCEPTOR = new BrokerInterceptor(EMPTY_OBSERVERS);
    
    DummyChannel m_session;
    ConnectMessage connMsg;
    ProtocolProcessor m_processor;
    
    IMessagesStore m_storageService;
    ISessionsStore m_sessionStore;
    SubscriptionsStore subscriptions;
    MockAuthenticator m_mockAuthenticator;
    
    IBrokerOperator brokerOperator;
    
    @Before
    public void setUp() throws InterruptedException {
        connMsg = new ConnectMessage();
        connMsg.setProtocolVersion((byte) 0x03);

        m_session = new DummyChannel();

        //sleep to let the messaging batch processor to process the initEvent
        Thread.sleep(300);
        MemoryStorageService memStorage = new MemoryStorageService();
        m_storageService = memStorage;
        m_sessionStore = memStorage;
        m_storageService.initStore();
        
        Map<String, byte[]> users = new HashMap<>();
        users.put(TEST_USER, TEST_PWD);
        m_mockAuthenticator = new MockAuthenticator(users);

        subscriptions = new SubscriptionsStore();
        subscriptions.init(new MemoryStorageService());
        m_processor = new ProtocolProcessor();
        m_processor.init(subscriptions, m_storageService, m_sessionStore, m_mockAuthenticator, true,
                new PermitAllAuthorizator(), null);
        
        this.brokerOperator = new MoquetteOperator(m_processor);
    }
    
    /**
     * Verify that receiving a publish with retained message and with Q0S = 0 
     * clean the existing retained messages for that topic.
     * @throws InterruptedException 
     */
    @Test
    public void testCountRetainedMessages() {
        ByteBuffer payload1 = ByteBuffer.allocate(32).put("Hello".getBytes());
        ByteBuffer payload2 = ByteBuffer.allocate(32).put("Hello".getBytes());
        m_processor.getMessagesStore().storeRetained(FAKE_TOPIC, payload1, QOSType.LEAST_ONE);
        m_processor.getMessagesStore().storeRetained(FAKE_SUBTOPIC, payload2, QOSType.LEAST_ONE);
    
        assertTrue(brokerOperator.countRetainedMessages(FAKE_TOPICANDSUBTOPICFILTER)==2);
        assertTrue(brokerOperator.countRetainedMessages(FAKE_TOPIC)==1);
        assertTrue(brokerOperator.countRetainedMessages(FAKE_SUBTOPIC)==1);
    }

}
