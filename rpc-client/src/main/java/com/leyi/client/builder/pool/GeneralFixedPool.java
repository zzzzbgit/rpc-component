package com.leyi.client.builder.pool;

import com.leyi.client.builder.pool.exception.TimeoutException;
import com.leyi.client.builder.pool.element.PoolElement;
import com.leyi.client.builder.pool.exception.PoolException;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * FixedPool
 *
 * @param <T> key
 * @param <E> element
 */
public class GeneralFixedPool<T, E extends PoolElement> extends AbstractFixedPool<E> {

    /*FixedPool maxConnections*/
    private final int maxConnections;

    /*createElement Function*/
    private final Function<T, E> createElementFunc;

    /**
     * constructor
     *
     * @param maxConnections    max elements
     * @param createElementFunc create element function
     * @param param             param
     */
    public GeneralFixedPool(int maxConnections, Function<T, E> createElementFunc, T param) {
        this(maxConnections, createElementFunc, param, DEFAULT_ACQUIRE_TIMEOUT);
    }

    /**
     * constructor
     *
     * @param maxConnections    max elements
     * @param createElementFunc create element function
     * @param param             param
     * @param acquireTimeout    acquire timeout
     */
    public GeneralFixedPool(int maxConnections, Function<T, E> createElementFunc, T param, int acquireTimeout) {
        this(maxConnections, createElementFunc, param, acquireTimeout, DEFAULT_RESET_TIMES);
    }

    /**
     * constructor
     *
     * @param maxConnections    max elements
     * @param createElementFunc create element function
     * @param param             param
     * @param acquireTimeout    acquire timeout
     * @param resetTimes        reset times
     */
    public GeneralFixedPool(int maxConnections, Function<T, E> createElementFunc, T param, int acquireTimeout, int resetTimes) {
        super(new LinkedBlockingQueue<E>(maxConnections));
        this.resetTimes = resetTimes;
        this.acquireTimeout = acquireTimeout;
        this.maxConnections = maxConnections;
        this.createElementFunc = createElementFunc;
        create(param);
    }

    /**
     * create fill pool
     *
     * @param key
     */
    private void create(T key) {
        int currentSize = pendingAcquireQueue.size();
        for (int i = 0; i < this.maxConnections - currentSize; i++) {
            pendingAcquireQueue.offer(this.createElementFunc.apply(key));
        }
    }

    /**
     * acquire element
     *
     * @return
     */
    @Override
    public E acquire() {
        E element = null;
        try {
            element = pendingAcquireQueue.poll(acquireTimeout, TimeUnit.SECONDS);
            if (element.isHealth()) {
                return element;
            } else {
                //连接不正常处理
                int retryCount = 0;
                while (retryCount < DEFAULT_RESET_TIMES) {
                    try {
                        element.reset();
                        return element;
                    } catch (PoolException pe) {
                        retryCount++;
                    }
                }
                throw new PoolException("FixedPool.acquire.retry Error!");
            }
        } catch (InterruptedException e) {
            throw new TimeoutException("FixedPool.acquire.queue Timeout Error!", e);
        }
    }

    /**
     * release element
     *
     * @param element element
     */
    @Override
    public void release(PoolElement element) {
        pendingAcquireQueue.offer((E) element);
    }

    public int getMaxConnections() {
        return this.maxConnections;
    }

    /**
     * close pool
     */
    @Override
    public void close() {
        for (int i = 0; i < pendingAcquireQueue.size(); i++) {
            E element = null;
            try {
                element = pendingAcquireQueue.take();
            } catch (InterruptedException e) {
                throw new PoolException("FixedPool.close.error!", e);
            }
            element.close();
        }
        this.closed = true;
    }
}
