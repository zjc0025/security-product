package com.zjc.social.service;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import com.zjc.social.model.ChatMessages;
import com.zjc.social.utils.RedisLockUtil;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName ChatService
 * @Description
 * @Author ZJC
 * @Date 2020/8/3 8:12
 */
@Service
public class ChatService {

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * @param sender, recipients, message, chatId
     * @return java.lang.String
     * @author ZJC
     * @Description 创建聊天
     * @Date 2020/8/3 9:20
     **/
    public String createChat(String sender, Set<String> recipients, String message) {
        recipients.add(sender);
        String chatId = String.valueOf(redisTemplate.opsForValue().increment("ids:chat:"));
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                for (String recipient : recipients) {
                    //初始化聊天组 添加组员和组员对应的最大消息id
                    redisTemplate.opsForZSet().add("chat:" + chatId, recipient, 0);
                    //初始化用户参加的各个群组，以及用户在这些群组中已读的最大消息id
                    redisTemplate.opsForZSet().add("seen:" + recipient, chatId, 0);
                }
                operations.exec();
                return null;
            }
        });

        return sendMessage(chatId, sender, message);
    }

    /**
     * @param chatId, sender, message
     * @return java.lang.String
     * @author ZJC
     * @Description 向群组发送消息
     * @Date 2020/8/3 9:21
     **/
    public String sendMessage(String chatId, String sender, String message) {
        RLock lock = RedisLockUtil.lock("chat:" + UUID.randomUUID());

        try {
            long messageId = redisTemplate.opsForValue().increment("ids:" + chatId);
            HashMap<String, Object> values = new HashMap<String, Object>();
            values.put("id", messageId);
            values.put("ts", System.currentTimeMillis());
            values.put("sender", sender);
            values.put("message", message);

            String packed = JSON.toJSONString(values);

            redisTemplate.opsForZSet().add("msgs:" + chatId, packed, messageId);
        } finally {
            lock.unlock();
        }
        return chatId;
    }


    /**
     * @param recipient
     * @return java.util.List<com.zjc.social.model.ChatMessages>
     * @author ZJC
     * @Description 抓取当前用户的各群组未读消息
     * @Date 2020/8/3 10:08
     **/
    @SuppressWarnings("unchecked")
    public List<ChatMessages> fetchPendingMessages(String recipient) {
        Set<ZSetOperations.TypedTuple> seenSet = redisTemplate.opsForZSet().rangeWithScores("seen:" + recipient, 0, -1);
//        Set<RedisZSetCommands.Tuple> seenSet = conn.zrangeWithScores("seen:" + recipient, 0, -1);
        List<ZSetOperations.TypedTuple> seenList = new ArrayList<>(seenSet);


        List<Object> results = (List<Object>) redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();

                for (ZSetOperations.TypedTuple tuple : seenList) {
                    String chatId = (String) tuple.getValue();
                    int seenId = tuple.getScore().intValue();
                    redisTemplate.opsForZSet().rangeByScore("msgs:" + chatId, seenId + 1, Double.MAX_VALUE);
//                    trans.zrangeByScore("msgs:" + chatId, String.valueOf(seenId + 1), "inf");
                }

                return operations.exec();
            }
        });


        Iterator<ZSetOperations.TypedTuple> seenIterator = seenList.iterator();
        Iterator<Object> resultsIterator = results.iterator();

        List<ChatMessages> chatMessages = new ArrayList<>();
        List<Object[]> seenUpdates = new ArrayList<>();
        List<Object[]> msgRemoves = new ArrayList<>();
        while (seenIterator.hasNext()) {
            ZSetOperations.TypedTuple seen = seenIterator.next();
            Set<String> messageStrings = (Set<String>) resultsIterator.next();
            if (messageStrings.size() == 0) {
                continue;
            }

            int seenId = 0;
            String chatId = (String) seen.getValue();
            List<Map<String, Object>> messages = new ArrayList<>();
            for (String messageJson : messageStrings) {
//                Map<String, Object> message = (Map<String, Object>) gson.fromJson(
//                        messageJson, new TypeToken<Map<String, Object>>() {
//                        }.getType());
                Map<String, Object> message = JSONObject.parseObject(messageJson);
                int messageId = (int) message.get("id");
                if (messageId > seenId) {
                    seenId = messageId;
                }
                message.put("id", messageId);
                messages.add(message);
            }

//            conn.zadd("chat:" + chatId, seenId, recipient);
            redisTemplate.opsForZSet().add("chat:" + chatId, recipient, seenId);
            seenUpdates.add(new Object[]{"seen:" + recipient, seenId, chatId});

            //获取该群组所有人都已读的最大消息id
            Set<ZSetOperations.TypedTuple> minIdSet = redisTemplate.opsForZSet().rangeWithScores("chat:" + chatId, 0, 0);
//            Set<RedisZSetCommands.Tuple> minIdSet = conn.zrangeWithScores("chat:" + chatId, 0, 0);
            if (minIdSet.size() > 0) {
                msgRemoves.add(new Object[]{
                        "msgs:" + chatId, minIdSet.iterator().next().getScore()});
            }
            chatMessages.add(new ChatMessages(chatId, messages));
        }


        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                for (Object[] seenUpdate : seenUpdates) {
                    redisTemplate.opsForZSet().add(seenUpdate[0],
                            seenUpdate[2],
                            Double.parseDouble(String.valueOf(seenUpdate[1]))
                    );
                }
                for (Object[] msgRemove : msgRemoves) {
//                    trans.zremrangeByScore(
//                            (String) msgRemove[0], 0, ((Double) msgRemove[1]).intValue());
                    redisTemplate.opsForZSet().removeRangeByScore(msgRemove[0], 0, (Double) msgRemove[1]);
                }
                operations.exec();
                return null;
            }
        });


        return chatMessages;
    }


    /**
     * @param chatId, user
     * @return void
     * @author ZJC
     * @Description 加入群组
     * @Date 2020/8/3 15:57
     **/
    public void joinChat(String chatId, String user) {
        //获取该群组最大消息id
        Integer messageId = (Integer) redisTemplate.opsForValue().get("ids:"+chatId);
        //放入用户已读有序集合
        redisTemplate.opsForZSet().add("seen:"+user, chatId, messageId);
        //将该用户放入群组有序集合
        redisTemplate.opsForZSet().add("chat:"+chatId, user, messageId);
    }

    /**
     * @param chatId, user
     * @return void
     * @author ZJC
     * @Description 离开群组
     * @Date 2020/8/3 15:57
     **/
    public void leaveChat(String chatId, String user) {
        //删除用户已读中的该群组
        redisTemplate.opsForZSet().remove("seen:"+user, chatId);
        //删除群组中的该用户
        redisTemplate.opsForZSet().remove("chat:"+chatId, user);
        //如果该群没有成员，则删除该群组id计数器和群组消息有序集合
        long chatMember = redisTemplate.opsForZSet().zCard("chat:"+chatId);
        if(chatMember == 0){
            redisTemplate.delete("msgs:"+chatId);
            redisTemplate.delete("ids:"+chatId);
        }
    }

}
