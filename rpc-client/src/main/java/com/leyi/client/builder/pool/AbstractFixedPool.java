package com.leyi.client.builder.pool;

import com.leyi.client.builder.pool.element.PoolElement;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class AbstractFixedPool<E extends PoolElement> implements AbstractPool<E> {

    /*pendingAcquireQueue*/
    protected final LinkedBlockingQueue<E> pendingAcquireQueue;

    /*default reset times*/
    public static final int DEFAULT_RESET_TIMES = 3;
    /*default acquire time*/
    public static final int DEFAULT_ACQUIRE_TIMEOUT = 30;
    /*acquire time*/
    protected int acquireTimeout;
    /*reset*/
    protected int resetTimes;
    /*closed flag*/
    protected boolean closed;

    protected AbstractFixedPool(LinkedBlockingQueue<E> pendingAcquireQueue) {
        this.pendingAcquireQueue = pendingAcquireQueue;
    }

    public abstract E acquire();

    public abstract void release(PoolElement element);

    public abstract void close();

    public boolean isClosed() {
        return closed;
    }

}
