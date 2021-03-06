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
package io.moquette.plugin;

import java.util.*;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import io.moquette.interception.InterceptHandler;
import io.moquette.plugin.IBrokerOperator;
import io.moquette.plugin.MoquetteOperator;
import io.moquette.proto.messages.*;
import io.moquette.spi.IMessagesStore;
import io.moquette.spi.ISessionsStore;
import io.moquette.spi.impl.DummyChannel;
import io.moquette.spi.impl.MemoryStorageService;
import io.moquette.spi.impl.MockAuthenticator;
import io.moquette.spi.impl.ProtocolProcessor;
import io.moquette.spi.impl.security.PermitAllAuthorizator;
import io.moquette.spi.impl.subscriptions.SubscriptionsStore;

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
    
    IMessagesStore m_messagesStore;
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
        memStorage.initStore();
        m_messagesStore = memStorage.messagesStore();
        m_sessionStore = memStorage.sessionsStore();
        //m_messagesStore.initStore();
        
        Map<String, byte[]> users = new HashMap<>();
        users.put(TEST_USER, TEST_PWD);
        m_mockAuthenticator = new MockAuthenticator(users);

        subscriptions = new SubscriptionsStore();
        subscriptions.init(memStorage.sessionsStore());
        m_processor = new ProtocolProcessor();
        m_processor.init(subscriptions, m_messagesStore, m_sessionStore, m_mockAuthenticator, true,
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
        m_processor.getMessagesStore().storeRetained(FAKE_TOPIC,"2");
        m_processor.getMessagesStore().storeRetained(FAKE_SUBTOPIC,"3");
        
        assertTrue(brokerOperator.countRetainedMessages(FAKE_TOPICANDSUBTOPICFILTER)==2);
        assertTrue(brokerOperator.countRetainedMessages(FAKE_TOPIC)==1);
        assertTrue(brokerOperator.countRetainedMessages(FAKE_SUBTOPIC)==1);
    }

}
