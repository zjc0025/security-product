package com.zjc.security.web.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisUtilTest {

    @Autowired
    RedisUtil redisUtil;

    @Test
    public void testKeys(){
        System.out.println(redisUtil.keys("*"));
    }

    @Test
    public void testExpire(){
        System.out.println(redisUtil.expire("java",30));
    }

    @Test
    public void testGetExpire(){
        System.out.println(redisUtil.getExpire("java"));
    }

    @Test
    public void testHasKey(){
        System.out.println(redisUtil.hasKey("java"));
    }

    @Test
    public void testDelKey(){
        redisUtil.del("java","aaa");
    }

    @Test
    public void testGetKey(){
        System.out.println(redisUtil.get("java"));
    }

    @Test
    public void testSetKey(){
        System.out.println(redisUtil.set("java",111));
    }

    @Test
    public void testSetAndExpireKey(){
        System.out.println(redisUtil.set("java2",222,60));
    }

    @Test
    public void testIncr(){
        System.out.println(redisUtil.incr("java",1));
    }

    @Test
    public void testDecr(){
        System.out.println(redisUtil.decr("java",1));
    }

    @Test
    public void testGetHash(){
        Map map = new HashMap();
        map.put("a","1");
        map.put("b","2");
        System.out.println(redisUtil.hmset("hashMap",map));
    }

    @Test
    public void testGetHashField(){
        System.out.println(redisUtil.hget("hashMap","a"));
    }

    @Test
    public void testhmget(){
        System.out.println(redisUtil.hmget("hashMap"));
    }

    @Test
    public void testhset(){
        System.out.println(redisUtil.hset("hashMap","c",3));
    }

    @Test
    public void testhdel(){
        redisUtil.hdel("hashMap","c");
    }

    @Test
    public void testhHasKey(){
        System.out.println(redisUtil.hHasKey("hashMap","c"));
    }

    @Test
    public void testhincr(){
        System.out.println(redisUtil.hincr("hashMap","c",2));
    }

    @Test
    public void testsSet(){
        System.out.println(redisUtil.sSet("set2",1,2,2));
    }

    @Test
    public void testsGet(){
        System.out.println(redisUtil.sGet("set2"));
    }

    @Test
    public void testsHasKey(){
        System.out.println(redisUtil.sHasKey("set2",1));
    }

    @Test
    public void testsGetSetSize(){
        System.out.println(redisUtil.sGetSetSize("set2"));
    }

    @Test
    public void testsetRemove(){
        System.out.println(redisUtil.setRemove("set2",1,4));
    }

    @Test
    public void testlSet(){
        System.out.println(redisUtil.listRightPush("list",111));
    }

    @Test
    public void testlSetAll(){
        String[] array = {"2","5","7"};
        System.out.println(redisUtil.lSet("list", CollectionUtils.arrayToList(array)));
    }

    @Test
    public void testlUpdateIndex(){
        System.out.println(redisUtil.lUpdateIndex("list",0,666));
    }

    @Test
    public void testlRemove(){
        System.out.println(redisUtil.lRemove("list",2,111));
    }

    @Test
    public void testzsetAdd(){
        System.out.println(redisUtil.zsetAdd("zset","aaa",111));
    }

    @Test
    public void testzsetAddTuple(){
        Set<ZSetOperations.TypedTuple<Object>> zset = new HashSet<>();
        ZSetOperations.TypedTuple typedTuple1 = new DefaultTypedTuple("bbb", 222.0);
        zset.add(typedTuple1);
        System.out.println(redisUtil.zsetAddTuple("zset",zset));
    }

    @Test
    public void testzsetRemove(){
        System.out.println(redisUtil.zsetRemove("zset","aaa"));
    }

}