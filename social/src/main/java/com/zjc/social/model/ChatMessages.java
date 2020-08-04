package com.zjc.social.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName ChatMessages
 * @Description
 * @Author ZJC
 * @Date 2020/8/3 10:05
 */
@Data
public class ChatMessages {
    public String chatId;
    public List<Map<String, Object>> messages;

    public ChatMessages(String chatId, List<Map<String, Object>> messages) {
        this.chatId = chatId;
        this.messages = messages;
    }

    public boolean equals(Object other) {
        if (!(other instanceof ChatMessages)) {
            return false;
        }
        ChatMessages otherCm = (ChatMessages) other;
        return chatId.equals(otherCm.chatId) &&
                messages.equals(otherCm.messages);
    }

    @Override
    public String toString() {
        return "ChatMessages{" +
                "chatId='" + chatId + '\'' +
                ", messages=" + messages +
                '}';
    }
}
