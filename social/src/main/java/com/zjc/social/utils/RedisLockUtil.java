package com.zjc.social.utils;

import org.redisson.api.RLock;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName RedisLockUtil
 * @Description
 * @Author ZJC
 * @Date 2020/7/29 10:57
 */
public class RedisLockUtil {

    private static DistributedLocker distributedLocker = SpringContextHolder.getBean(DistributedLocker.class);

    /**
     * 加锁
     *
     * @param lockKey
     * @return
     */
    public static RLock lock(String lockKey) {
        return distributedLocker.lock(lockKey);
    }

    /**
     * 释放锁
     *
     * @param lockKey
     */
    public static void unlock(String lockKey) {
        distributedLocker.unlock(lockKey);
    }

    /**
     * 释放锁
     *
     * @param lock
     */
    public static void unlock(RLock lock) {
        distributedLocker.unlock(lock);
    }

    /**
     * 带超时的锁
     *
     * @param lockKey
     * @param timeout 超时时间   单位：秒
     */
    public static RLock lock(String lockKey, int timeout) {
        return distributedLocker.lock(lockKey, timeout);
    }

    /**
     * 带超时的锁
     *
     * @param lockKey
     * @param unit    时间单位
     * @param timeout 超时时间
     */
    public static RLock lock(String lockKey, int timeout, TimeUnit unit) {
        return distributedLocker.lock(lockKey, unit, timeout);
    }

    /**
     * 尝试获取锁
     *
     * @param lockKey
     * @param waitTime  最多等待时间
     * @param leaseTime 上锁后自动释放锁时间
     * @return
     */
    public static boolean tryLock(String lockKey, int waitTime, int leaseTime) {
        return distributedLocker.tryLock(lockKey, TimeUnit.SECONDS, waitTime, leaseTime);
    }

    /**
     * 尝试获取锁
     *
     * @param lockKey
     * @param unit      时间单位
     * @param waitTime  最多等待时间
     * @param leaseTime 上锁后自动释放锁时间
     * @return
     */
    public static boolean tryLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime) {
        return distributedLocker.tryLock(lockKey, unit, waitTime, leaseTime);
    }

    /**
     * 获取计数器
     *
     * @param name
     * @return
     */
//    public static RCountDownLatch getCountDownLatch(String name){
//        return distributedLocker.getCountDownLatch(name);
//    }

    /**
     * 获取信号量
     *
     * @param name
     * @return
     */
//    public static RSemaphore getSemaphore(String name){
//        return distributedLocker.getSemaphore(name);
//    }
}
