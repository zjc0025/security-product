package com.zjc.social.service;

import com.zjc.social.model.ChatMessages;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ChatServiceTest {

    @Autowired
    ChatService chatService;

    @Test
    public void testCreateChat(){
        Set<String> recipients = new HashSet<>();
        recipients.add("jeff");
        recipients.add("jenny");
        String chatId = chatService.createChat("zjc", recipients, "你好4");
        System.out.println(chatId);
    }

    @Test
    public void testSendMessage(){
        chatService.sendMessage("2","测试", "ceshi1");
    }

    @Test
    public void testFetchPendingMessages(){
        List<ChatMessages> msgs = chatService.fetchPendingMessages("ttt");

        for(ChatMessages chatMessages : msgs){
            System.out.println(chatMessages.getChatId());

            System.out.println("--"+chatMessages.getMessages());
        }
    }

    @Test
    public void testJoinChat(){
        chatService.joinChat("2", "ttt");
    }

}