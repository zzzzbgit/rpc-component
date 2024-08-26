package com.shudun.client.pool;



import com.shudun.client.pool.element.PoolElement;
import com.shudun.client.pool.exception.PoolException;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

/**
 * 固定池
 *
 * @param <K> 创建链接参数类型
 * @param <R>
 */
public class FixedPool<K, R extends PoolElement> {

    /**
     * 池最大大小
     */
    private final int maxPool;

    /**
     * pool队列
     */
    private final LinkedBlockingQueue<R> pool;

    /**
     * 是否已关闭
     */
    private boolean closed;

    /**
     * 默认重置次数
     */
    private final int DEFAULT_RESET_TIMES = 3;


    /**
     * map创建
     *
     * @param maxPool  pool大小
     * @param function 创建 element 方法
     * @param key      创建 element 参数
     * @param <K>
     * @param <R>
     * @return
     */
    public static <K, R extends PoolElement> FixedPool<K, R> build(int maxPool, Function<K, R> function, K key) {
        FixedPool<K, R> fixedPool = new FixedPool<>(maxPool);
        fixedPool.create(function, key);
        return fixedPool;
    }

    /**
     * 构造方法
     *
     * @param maxPool pool大小
     */
    private FixedPool(int maxPool) {
        this.closed = false;
        this.maxPool = maxPool;
        this.pool = new LinkedBlockingQueue<>(maxPool);
    }

    /**
     * 创建pool
     *
     * @param function
     * @param key
     */
    private void create(Function<K, R> function, K key) {
        for (int i = 0; i < this.maxPool; i++) {
            pool.offer(function.apply(key));
        }
    }

    /**
     * 获取元素
     *
     * @return
     */
    public R acquire() {
        try {
            R r = pool.take();
            if (r.isHealth()) {
                return r;
            } else {
                //连接不正常处理
                int retryCount = 0;
                while (retryCount < DEFAULT_RESET_TIMES) {
                    try {
                        r.reSet();
                        return r;
                    } catch (PoolException pe) {
                        retryCount++;
                    }
                }
                throw new PoolException("FixedPool.acquire.retry Error!");
            }
        } catch (InterruptedException e) {
            throw new PoolException("FixedPool.acquire.error!", e);
        }
    }

    /**
     * 释放
     *
     * @param element
     */
    public void release(R element) {
        pool.offer(element);
    }

    public boolean isClosed() {
        return closed;
    }

    public int getMaxPoolSize() {
        return maxPool;
    }

    /**
     * 关闭
     */
    public void close() {
        for (int i = 0; i < maxPool; i++) {
            R r = null;
            try {
                r = pool.take();
            } catch (InterruptedException e) {
                throw new PoolException("FixedPool.close.error!", e);
            }
            r.close();
        }
        this.closed = true;
    }
}
