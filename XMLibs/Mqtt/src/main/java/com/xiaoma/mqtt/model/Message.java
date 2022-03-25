package com.xiaoma.mqtt.model;

/**
 * Created by Thomas on 2019/1/17 0017
 */

public class Message {

    private String topic;
    private String content;

    public Message(String topic, String content) {
        this.topic = topic;
        this.content = content;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "topic == " + topic + " , content ==" + content;
    }
}