package com.zjc.social.service;

import com.zjc.social.utils.RedisLockUtil;
import com.zjc.social.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName UserService
 * @Description
 * @Author ZJC
 * @Date 2020/7/30 13:44
 */
@Slf4j
@Service
public class UserService {

    private static final long POSTS_PER_PASS = 1000;

    private static final long HOME_TIMELINE_SIZE = 100;

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


        redisTemplate.execute(new SessionCallback<Object>() {

            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                //开启事务
                operations.multi();
                //将该帐号与id绑定放入hash
                redisUtil.hset("users:", llogin, String.valueOf(id));
                //初始化用户信息
                Map<String, Object> values = new HashMap<>();
                values.put("login", login);
                values.put("id", String.valueOf(id));
                values.put("name", name);
                values.put("followers", 0);
                values.put("following", 0);
                values.put("posts", 0);
                values.put("signup", String.valueOf(System.currentTimeMillis()));

                redisTemplate.opsForHash().putAll("user:" + id, values);
                //执行事务
                operations.exec();
                return null;
            }
        });

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
    public long createStatus(long userId, String message, Map<String, Object> data) {

        final List[] response = new List[]{new ArrayList<>()};

        redisTemplate.execute(new SessionCallback<Object>() {

            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                //开启事务
                operations.multi();
                //根据用户id获取账号名
                redisTemplate.opsForHash().get("user:" + userId, "login");
                //获取新的状态id
                redisTemplate.opsForValue().increment("status:id:", 1);
                //执行事务
                response[0] = operations.exec();
                return null;
            }
        });


        //获取帐号名
        String login = (String) response[0].get(0);
        //获取用户id
        long id = (Long) response[0].get(1);
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


        Map<String, Object> finalData = data;
        redisTemplate.execute(new SessionCallback<Object>() {

            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                //开启事务
                operations.multi();
                //使用hash保存状态id和信息内容
                redisTemplate.opsForHash().putAll("status:" + id, finalData);
                //用户表的发送信息数量加1
                redisTemplate.opsForHash().increment("user:" + userId, "posts", 1);
                //执行事务
                operations.exec();
                return null;
            }
        });

        return id;
    }

    /**
     * @param userId, timeline, pageIndex, pageSize
     * @return java.util.List<java.lang.String>
     * @author ZJC
     * @Description 根据用户id、时间轴名称、页数、每页条数,获取时间轴消息
     * @Date 2020/7/30 15:25
     **/
    public List<Map> getTimeline(String userId, String timeline, int pageIndex, int pageSize) {
        //时间倒序获取分页消息id
        Set<Object> msg = redisTemplate.opsForZSet().reverseRange(timeline + userId, (pageIndex - 1) * pageSize, pageIndex * pageSize - 1);
//        List<String> msgList = new ArrayList<>();
        List<Map> msgList = new ArrayList<>();
        if (null != msg) {
            //循环消息id获取消息具体内容
//            msg.forEach(x -> msgList.add((String) redisTemplate.opsForHash().get("status:" + x, "message")));
            msg.forEach(x -> msgList.add(redisTemplate.opsForHash().entries("status:" + x)));
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
        long homeSize = Optional.ofNullable(redisTemplate.opsForZSet().zCard("home:" + userId)).orElse(0L);
        if (homeSize > 100) {
            redisTemplate.opsForZSet().removeRange("home:" + userId, 0, homeSize - 100);
        }

    }


    /**
     * @param userId, otherUserId
     * @return void
     * @author ZJC
     * @Description 取消关注
     * @Date 2020/7/30 16:17
     **/
    public void unFollowUser(String userId, String otherUserId) {
        //正在关注
        String fkey1 = "following:" + userId;
        //关注者
        String fkey2 = "followers:" + otherUserId;
        //判断是否已经取消关注
        if (redisTemplate.opsForZSet().score(fkey1, otherUserId) == null) {
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
        if (null != msg && msg.size() > 0) {
            for (ZSetOperations.TypedTuple tmp : msg) {
                redisTemplate.opsForZSet().remove("home:" + userId, tmp.getValue());
            }
        }

    }

    /**
     * @param userId, message, data
     * @return void
     * @author ZJC
     * @Description 更新个人时间轴
     * @Date 2020/7/31 8:03
     **/
    public long postStatus(long userId, String message, Map<String, Object> data) {
        long id = createStatus(userId, message, data);
        if (id < 0) {
            return -1;
        }
        String time = (String) redisTemplate.opsForHash().get("status:" + id, "posted");
        if (null == time) {
            return -1;
        }
        //更新本人时间轴
        redisTemplate.opsForZSet().add("profile:" + userId, id, Double.parseDouble(time));
        //更新关注者主页时间轴
        syndicateStatus(userId, id, Double.parseDouble(time), 0);
        return id;
    }

    /**
     * @param userId, posted, start
     * @return void
     * @author ZJC
     * @Description 更新关注者主页时间轴
     * @Date 2020/7/31 8:14
     **/
    public void syndicateStatus(long userId, long postId, double postTime, double start) {
        //获取1000个关注者
        Set<ZSetOperations.TypedTuple<Object>> followers = redisTemplate.opsForZSet().rangeByScoreWithScores(
                "followers:" + userId,
                start, Double.MAX_VALUE,
                0, POSTS_PER_PASS);


        start = (double) redisTemplate.execute(new SessionCallback<Object>() {

            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                double index = 0;
                //开启事务
                operations.multi();
                for (ZSetOperations.TypedTuple tuple : followers) {
                    String follower = (String) tuple.getValue();
                    index = tuple.getScore();
                    redisTemplate.opsForZSet().add("home:" + follower, postId, postTime);
                    redisTemplate.opsForZSet().range("home:" + follower, 0, -1);
                    redisTemplate.opsForZSet().removeRange(
                            "home:" + follower, 0, 0 - HOME_TIMELINE_SIZE - 1);
                }
                //执行事务
                operations.exec();
                return index;
            }
        });


        //如果关注者大于1000，则启动线程后台更新关注者的时间轴
        if (followers.size() >= POSTS_PER_PASS) {
            try {
                ExecutorService executor = Executors.newFixedThreadPool(10);
                double finalStart = start;
                executor.submit(() -> {
                    syndicateStatus(userId, postId, postTime, finalStart);
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    public boolean deleteStatus(long uid, long statusId) {
        String key = "status:" + statusId;
        String lockKey = key + UUID.randomUUID();
        RedisLockUtil.lock(lockKey);

        try {
            //如果该消息的发布人不是uid，删除失败
            if (!String.valueOf(uid).equals(redisTemplate.opsForHash().get(key, "uid"))) {
                return false;
            }

            redisTemplate.execute(new SessionCallback<Object>() {

                @Override
                public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                    //开启事务
                    operations.multi();
                    //删除该消息
                    redisTemplate.delete(key);
                    //删除该用户个人时间轴中此消息的id
                    redisTemplate.opsForZSet().remove("profile:" + uid, statusId);
                    //删除该用户主页时间轴中此消息的id
                    redisTemplate.opsForZSet().remove("home:" + uid, statusId);
                    //该用户发布消息数-1
                    redisTemplate.opsForHash().increment("user:" + uid, "posts", -1);
                    //执行事务
                    operations.exec();
                    return null;
                }
            });

            return true;
        } finally {
            RedisLockUtil.unlock(lockKey);
        }
    }

}
