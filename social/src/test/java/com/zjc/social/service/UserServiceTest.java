package com.zjc.social.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author ZJC
 * @Description 社交网站基础测试
 * @Date 2020/7/31 9:11
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    public void testCreateUser() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long userId = userService.createUser("zjc2", "测试");
                System.out.println("创建用户id为：" + userId);
                countDownLatch.countDown();
            });
        }

        countDownLatch.countDown();
        Thread.sleep(5000);
    }

    @Test
    public void testCreateStatus() {
        long statusId = userService.createStatus(1, "你好", null);
        System.out.println(statusId);
    }

    @Test
    public void testPostStatus() {
//        long statusId = userService.postStatus(1,"你好o", null);
        long statusId = userService.postStatus(1, "663你好o", null);
        System.out.println(statusId);
    }

    @Test
    public void testGetTimeline() {
//        List<Map> timeline = userService.getTimeline("2","home:", 0,100);
        List<Map> timeline = userService.getTimeline("1", "profile:", 0, 100);
        System.out.println(timeline);
    }

    @Test
    public void testFollowUser() {
        userService.followUser("2", "1");
    }

    @Test
    public void testUnFollowUser() {
        userService.unFollowUser("2", "1");
    }

    @Test
    public void testDeleteStatus() {
        System.out.println(userService.deleteStatus(1, 2));
    }

}