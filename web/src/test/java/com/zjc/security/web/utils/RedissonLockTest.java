package com.zjc.security.web.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName RedissonLockTest
 * @Description
 * @Author ZJC
 * @Date 2020/7/29 11:12
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedissonLockTest {


    /**
     * 锁测试共享变量
     */
    private Integer lockCount = 10;

    /**
     * 无锁测试共享变量
     */
    private Integer count = 10;

    /**
     * 模拟线程数
     */
    private static int threadNum = 10;

    /**
     * 模拟并发测试加锁和不加锁
     *
     * @return
     */
    //模拟并发测试加锁和不加锁
    @Test
    public void lock() throws InterruptedException {
        // 计数器
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < threadNum; i++) {
            MyRunnable myRunnable = new MyRunnable(countDownLatch);
            Thread myThread = new Thread(myRunnable);
            myThread.start();
        }

        // 释放所有线程
        countDownLatch.countDown();
        Thread.sleep(4000);
    }

    /**
     * 加锁测试
     */
    private void testLockCount() {
        String lockKey = "lock-test";
        try {
            // 加锁，设置超时时间2s
            RedisLockUtil.lock(lockKey, 2, TimeUnit.SECONDS);
            lockCount--;
            System.out.println("加锁lockCount值：" + lockCount);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            // 释放锁
            RedisLockUtil.unlock(lockKey);
        }
    }

    /**
     * 无锁测试
     */
    private void testCount() {
        count--;
        System.out.println("无锁count值：" + count);
    }


    public class MyRunnable implements Runnable {
        /**
         * 计数器
         */
        final CountDownLatch countDownLatch;

        public MyRunnable(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                // 阻塞当前线程，直到计时器的值为0
                countDownLatch.await();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
            // 无锁操作
            testCount();
            // 加锁操作
            testLockCount();
        }

    }

}
