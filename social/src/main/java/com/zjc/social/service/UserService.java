package com.zjc.social.service;

import com.zjc.social.utils.RedisLockUtil;
import com.zjc.social.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName UserService
 * @Description
 * @Author ZJC
 * @Date 2020/7/30 13:44
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedisTemplate<Object, Object> redisTemplate;

    /**
     * @param login, name
     * @return long 用户id
     * @author ZJC
     * @Description 创建用户
     * @Date 2020/7/30 13:48
     **/
    public long createUser(String login, String name) {
        //小写登录帐号
        String llogin = login.toLowerCase();
        //给该帐号加锁，防止同时注册相同的帐号
        RLock lock = RedisLockUtil.lock("user:" + llogin);
        //判断用户是否已绑定id
        if (redisUtil.hget("users:", llogin) != null) {
            return -1;
        }
        //生成用户id
        long id = redisUtil.incr("user:id:", 1);

        redisTemplate.multi();
        //将该帐号与id绑定放入hash
        redisUtil.hset("users:", llogin, String.valueOf(id));
        //初始化用户信息
        Map<String, String> values = new HashMap<>();
        values.put("login", login);
        values.put("id", String.valueOf(id));
        values.put("name", name);
        values.put("followers", "0");
        values.put("following", "0");
        values.put("posts", "0");
        values.put("signup", String.valueOf(System.currentTimeMillis()));

        redisTemplate.opsForHash().putAll("user:" + id, values);
        redisTemplate.exec();

        RedisLockUtil.unlock(lock);
        return id;
    }


    /**
     * @param userId, message, data
     * @return long 状态id
     * @author ZJC
     * @Description 创建用户状态
     * @Date 2020/7/30 14:30
     **/
    public long createStatus(long userId, String message, Map<String, String> data) {
        redisTemplate.multi();

        //根据用户id获取账号名
        redisTemplate.opsForHash().get("user:" + userId, "login");
        //获取新的状态id
        redisTemplate.opsForValue().increment("status:id:", 1);

        List<Object> response = redisTemplate.exec();
        //获取帐号名
        String login = (String) response.get(0);
        //获取用户id
        long id = (Long) response.get(1);
        //用户帐号不存在，则失败
        if (login == null) {
            return -1;
        }

        //初始化用户状态信息
        if (data == null) {
            data = new HashMap<>();
        }
        data.put("message", message);
        data.put("posted", String.valueOf(System.currentTimeMillis()));
        data.put("id", String.valueOf(id));
        data.put("uid", String.valueOf(userId));
        data.put("login", login);

        redisTemplate.multi();

        //使用hash保存状态id和信息内容
        redisTemplate.opsForHash().putAll("status:" + id, data);
        //用户表的发送信息数量加1
        redisTemplate.opsForHash().increment("user:" + userId, "posts", 1);

        redisTemplate.exec();
        return id;
    }

    /**
     * @param userId, timeline, pageIndex, pageSize
     * @return java.util.List<java.lang.String>
     * @author ZJC
     * @Description 根据用户id、时间轴名称、页数、每页条数,获取时间轴消息
     * @Date 2020/7/30 15:25
     **/
    public List<String> getTimeline(String userId, String timeline, int pageIndex, int pageSize) {
        //时间倒序获取分页消息id
        Set<Object> msg = redisTemplate.opsForZSet().reverseRange(timeline + userId, (pageIndex - 1) * pageSize, pageIndex * pageSize - 1);
        List<String> msgList = new ArrayList<>();
        if (null != msg) {
            //循环消息id获取消息具体内容
            msg.forEach(x -> msgList.add((String) redisTemplate.opsForHash().get("status:" + x, "message")));
        }
        return msgList;
    }


    /**
     * @param userId, otherUserId
     * @return void
     * @author ZJC
     * @Description 关注另一个用户
     * @Date 2020/7/30 15:36
     **/
    public void followUser(String userId, String otherUserId) {
        //正在关注
        String fkey1 = "following:" + userId;
        //关注者
        String fkey2 = "followers:" + otherUserId;
        //判断是否已关注
        if (redisTemplate.opsForZSet().score(fkey1, otherUserId) != null) {
            log.info("{} 已关注 {}", userId, otherUserId);
            return;
        }

        double currTime = (double) System.currentTimeMillis();
        //将被关注者的id和分值放到 关注者正在关注的集合中
        redisTemplate.opsForZSet().add(fkey1, otherUserId, currTime);
        //将关注者的id和分值放到 被关注者的关注者集合中
        redisTemplate.opsForZSet().add(fkey2, userId, currTime);
        //从被关注者的个人时间线里获取最新100条动态
        Set<ZSetOperations.TypedTuple<Object>> msg = redisTemplate.opsForZSet().reverseRangeWithScores("profile:" + otherUserId, 0, 100);
        //修改两个用户的hash，更新正在关注和被关注者的数量
        redisTemplate.opsForHash().increment("user:" + userId, "following", 1);
        redisTemplate.opsForHash().increment("user:" + otherUserId, "followers", 1);
        //对执行关注操作的用户的主页时间轴更新，保留最新的100条动态
        if (null != msg && msg.size() > 0) {
            redisTemplate.opsForZSet().add("home:" + userId, msg);
        }
        long end = Optional.ofNullable(redisTemplate.opsForZSet().zCard("home:" + userId)).orElse(0L);
        long start = end - 100;
        redisTemplate.opsForZSet().removeRange("home:" + userId, start, end);

    }


    /**
     * @author ZJC
     * @Description 取消关注
     * @Date 2020/7/30 16:17
     * @param userId, otherUserId
     * @return void
     **/
    public void unFollowUser(String userId, String otherUserId) {
        //正在关注
        String fkey1 = "following:" + userId;
        //关注者
        String fkey2 = "followers:" + otherUserId;
        //判断是否已经取消关注
        if (redisTemplate.opsForZSet().score(fkey1, otherUserId) != null) {
            log.info("{} 未关注 {}", userId, otherUserId);
            return;
        }
        //删除正在关注列表
        redisTemplate.opsForZSet().remove(fkey1, otherUserId);
        //删除关注者列表
        redisTemplate.opsForZSet().remove(fkey2, userId);
        //从需要取消的用户个人时间线里获取最新100条动态
        Set<ZSetOperations.TypedTuple<Object>> msg = redisTemplate.opsForZSet().reverseRangeWithScores("profile:" + otherUserId, 0, 100);
        //修改两个用户的hash，更新正在关注和被关注者的数量
        redisTemplate.opsForHash().increment("user:" + userId, "following", -1);
        redisTemplate.opsForHash().increment("user:" + otherUserId, "followers", -1);
        //删除被取消关注用户的100条动态
        if(null != msg && msg.size() > 0){
            for(ZSetOperations.TypedTuple tmp : msg){
                redisTemplate.opsForZSet().remove("home:" + userId, tmp.getValue());
            }
        }

    }

}
