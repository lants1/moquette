package io.moquette.interception.messages;

import java.nio.ByteBuffer;

import io.moquette.proto.messages.PublishMessage;

/**
 * @author Wagner Macedo
 */
public class InterceptPublishMessage extends InterceptAbstractMessage {
    private final PublishMessage msg;
    private final String clientID;

    public InterceptPublishMessage(PublishMessage msg, String clientID) {
        super(msg);
        this.msg = msg;
        this.clientID = clientID;
    }

    public String getTopicName() {
        return msg.getTopicName();
    }

    public ByteBuffer getPayload() {
        return msg.getPayload();
    }

    public String getClientID() {
        return clientID;
    }
}
