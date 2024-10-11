package com.leyi.client.builder.pool;


import com.leyi.client.builder.pool.element.PoolElement;

public interface AbstractPool<E> {
    E acquire();

    void release(PoolElement element);

    void close();
}
